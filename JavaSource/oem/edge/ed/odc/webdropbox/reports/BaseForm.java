package oem.edge.ed.odc.webdropbox.reports;

import org.apache.struts.action.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.*;
import java.text.*;
import java.lang.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2000-2005                                     */
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

public class BaseForm extends ActionForm {

   static protected final int YEAR  = 0;
   static protected final int MONTH = 1;
   static protected final int DAY   = 2;

   private boolean firsttime = true;
   public BaseForm() {
      startdate[YEAR] = "";
      startdate[MONTH] = "";
      startdate[DAY] = "";
      enddate[YEAR] = "";
      enddate[MONTH] = "";
      enddate[DAY] = "";
   }
   
  // Keeps a converted version of the specified field value until next reset
   Hashtable lhash = new Hashtable();
   public long getFieldValueAsLong(String field, String val) {
      synchronized(lhash) {
         Long l = (Long)lhash.get(field);
         if (l == null) {
            long ret = 0;
            try {
               ret = Long.decode(val.trim()).longValue();
            } catch(Exception e) {}
            l = new Long(ret);
            lhash.put(field, l);
         }
         return l.longValue();
      }
   }
   

  // Main
   public static final String User             = "User";
   public static final String Company          = "Company";
   
   public static final String SortField        = "SortField";
   public static final String SortDirection    = "SortDirection";
   public static final String ModifyFieldName  = "ModifyFieldName";
   public static final String ModifyFieldValue = "ModifyFieldValue";
   
  // Summary
   
  // Record
   public static final String RecordLev    = "RecordLev";
   
  // Hidden
   protected String sortField             = User;
   protected String sortDirection         = "ASC";
   
   protected String modifyFieldValue      = "";
   protected String modifyFieldName       = "";
   
  // include fields
   protected boolean user          = true;
   protected boolean company       = true;
   
  // include filters
   protected boolean userF         = false;
   protected boolean companyF      = false;
   
  // Record level or Summary
   protected String  recordlevG     = "summary";
   
  // Filter Values
   protected String userFV         = "";
   protected String companyFV      = "";
   
  // Filter Modifiers
   
  // Date processing
   protected String startdate[] = new String[3];
   protected String enddate[]   = new String[3];
   
   protected Vector years  = new Vector();
   protected Vector months = new Vector();
   protected Vector days   = new Vector();
   
  //
  // -- Setters and Getters
  //
  
   public String  getSortField()     { return sortField; }
   public void    setSortField(String v) { sortField = v;    }
    
   public String  getSortDirection()     { return sortDirection; }
   public void    setSortDirection(String v) { sortDirection = v;    }
  
   public String  getModifyFieldName()     { return modifyFieldName; }
   public void    setModifyFieldName(String v) { modifyFieldName = v;    }
   
   public String  getModifyFieldValue()     { return modifyFieldValue; }
   public void    setModifyFieldValue(String v) { modifyFieldValue = v;    }
  
   public boolean getUser()          { return user; }
   public void    setUser(boolean v) { user = v;    }
   
   public boolean getCompany()          { return company; }
   public void    setCompany(boolean v) { company = v;    }
   
  // include filter
   public boolean getUserFilter()          { return userF; }
   public void    setUserFilter(boolean v) { userF = v;    }
   
   public boolean getCompanyFilter()          { return companyF; }
   public void    setCompanyFilter(boolean v) { companyF = v;    }
   
  // include filter Values
   public String getUserFilterValue()          { return userFV; }
   public void    setUserFilterValue(String v) { userFV = v;    }
   
   public String getCompanyFilterValue()          { return companyFV; }
   public void    setCompanyFilterValue(String v) { companyFV = v;    }
   
  // include filter modifiers
   
  // reclev
   public String  getRecordLevel() { return recordlevG; }
   public void    setRecordLevel(String v) { recordlevG = v; }
   
  // start/end date
   public String  getStartYear() { return startdate[YEAR]; }
   public void    setStartYear(String v) { startdate[YEAR] = v; }
   public String  getStartMonth() { return startdate[MONTH]; }
   public void    setStartMonth(String v) { startdate[MONTH] = v; }
   public String  getStartDay() { return startdate[DAY]; }
   public void    setStartDay(String v) { startdate[DAY] = v; }
   
   
   public String  getEndYear() { return enddate[YEAR]; }
   public void    setEndYear(String v) { enddate[YEAR] = v; }
   public String  getEndMonth() { return enddate[MONTH]; }
   public void    setEndMonth(String v) { enddate[MONTH] = v; }
   public String  getEndDay() { return enddate[DAY]; }
   public void    setEndDay(String v) { enddate[DAY] = v; }
   
   public Vector  getYears()  {return years; }
   public Vector  getMonths() {return months;}
   public Vector  getDays()   {return days;  }
   
   public Date getStartDate() {
      return getStartDate(startdate);
   }
   
   public Date getEndDate() {
      return getEndDate(enddate);
   }
   
   public Date getStartDate(String date[]) {
      SimpleDateFormat fmt = new SimpleDateFormat("dd MM yyyy");
         
      String m = date[MONTH];
      String d = date[DAY];
      String y = date[YEAR];
         
     // This should never happen!
      Date startDate = null;
      if (m == null || d == null || y == null) {
         startDate = new Date();
      } else {
         if (m.length() == 1) m = "0" + m;
         if (d.length() == 1) d = "0" + d;
         try {
            startDate = fmt.parse(d + " " + m + " " + y);
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            cal.setLenient(true);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            startDate = cal.getTime();
         } catch(Exception ee) {}
      }
      return startDate;
   }
      
   public Date getEndDate(String date[]) {
      SimpleDateFormat fmt = new SimpleDateFormat("dd MM yyyy");
      
      String m = date[MONTH];
      String d = date[DAY];
      String y = date[YEAR];
         
     // This should never happen!
      Date endDate = null;
      if (m == null || d == null || y == null) {
         endDate = new Date();
      } else {
         if (m.length() == 1) m = "0" + m;
         if (d.length() == 1) d = "0" + d;
         try {
            endDate = fmt.parse(d + " " + m + " " + y);
            
            Calendar cal = Calendar.getInstance();
            cal.setLenient(true);
            cal.setTime(endDate);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            endDate = cal.getTime();
            
         } catch(Exception ee) {}
      }
      return endDate;
   }
   
   public boolean modifyField(String fieldname, String value) {
   
      boolean ret = true;
      
      if        (fieldname.equalsIgnoreCase(User)) {
         setUserFilter(true);
         setUserFilterValue("\"" + value + "\"");
      } else if (fieldname.equalsIgnoreCase(Company)) {
         setCompanyFilter(true);
         setCompanyFilterValue("\"" + value + "\"");
      } else {
         ret = false;
      }
      return ret;
   }
   
   
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
         
      setUser(false);
      setCompany(false);
      setUserFilter(false);
      setCompanyFilter(false);
      setRecordLevel("false");
      setUserFilterValue("");
      setCompanyFilterValue("");
      setStartYear("2000");
      setStartMonth("1");
      setStartDay("1");
      setEndYear("2000");
      setEndMonth("1");
      setEndDay("1");
      setRecordLevel("false");
      setSortField(User);
      setSortDirection("ASC");
      
      lhash.clear();
      
      if (firsttime) {
      
         firsttime = false;
         
         setUser(true);
         setCompany(true);
         setRecordLevel("true");
         
        // Initialize calendar bean info
        
         Calendar cal = Calendar.getInstance();
         cal.setTime(new Date());
         cal.setLenient(true);
         
        // fill in end date
         int curyear  = cal.get(Calendar.YEAR);
         int curmonth = cal.get(Calendar.MONTH);
         int curday   = cal.get(Calendar.DAY_OF_MONTH);
         setEndYear(""+curyear);
         setEndMonth(""+(curmonth+1));
         setEndDay(""+curday);
         
        // fill in start date
         cal.add(Calendar.MONTH, -1);
         cal.getTime();
         int syear  = cal.get(Calendar.YEAR);
         int smonth = cal.get(Calendar.MONTH);
         int sday   = cal.get(Calendar.DAY_OF_MONTH);
         setStartYear(""+syear);
         setStartMonth(""+(smonth+1));
         setStartDay(""+sday);
         
         if (years.size() == 0) {
           // fill in year options
            for(int i=2000; i <= curyear; i++) {
               years.addElement(new OptionBean(""+i, ""+i));
            }
            
           // Fill in Month options
            cal.set(Calendar.MONTH, 0);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            
            SimpleDateFormat formatter = 
               new SimpleDateFormat("MMM");
            
            for(int i=0; i < 12; i++) {
               String month = formatter.format(cal.getTime());
               months.addElement(new OptionBean(month, ""+(i+1)));
               cal.add(Calendar.MONTH, 1);
            }
            
           // fill in day options
            for(int i=1; i <= 31; i++) {
               days.addElement(new OptionBean(""+i, ""+i));
            }
         }
      }
   }
   
   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
      
      ActionErrors errors = super.validate(mapping, request);
      if (errors == null) errors = new ActionErrors();
      
      String s = getUserFilterValue();
      if (getUserFilter() && (s == null || s.trim().length() == 0)) {
         errors.add("user", new ActionError("error.userfilter"));
      }
      s = getCompanyFilterValue();
      if (getCompanyFilter() && (s == null || s.trim().length() == 0)) {
         errors.add("company", new ActionError("error.companyfilter"));
      }
      
      Date startdate = getStartDate();
      Date enddate   = getEndDate();
      if (startdate == null) {
         errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.startdate"));
      }
      if (enddate == null) {
         errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.enddate"));
      }
      
      if (startdate != null && enddate != null) {
         if (enddate.before(startdate)) {
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.endbeforestart"));
         }
         
         if (startdate.after(new Date())) {
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.startinfuture"));
         }
      }
      
      s = getSortField();
      if (s == null) {
         errors.add(ActionErrors.GLOBAL_ERROR, 
                    new ActionError("error.badsortfield"));
      }
      
      s = getSortDirection();
      if (s == null) {
         errors.add(ActionErrors.GLOBAL_ERROR, 
                    new ActionError("error.badsortdirection"));
      }
      
      return errors;
   }
   
  // If a filter name has a text field set but NO gval, its considered simple
   public static boolean fieldHasSimpleFilter(String name) {
      boolean ret = false;
      if (name.equals(User) || name.equals(Company)) {
         ret = true;
      }
      return ret;
   }
   
}
