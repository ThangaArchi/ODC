package oem.edge.ed.odc.webdropbox.reports;

import org.apache.struts.action.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.*;
import java.text.*;
import java.lang.*;

public class SessionForm extends BaseForm {

   private boolean firsttime = true;
   public SessionForm() {
   }

  // Access summary filter value fields as longs
   public long getAvgBytesUpAsLong() {
      return getFieldValueAsLong(AvgBytesUp, getAvgBytesUpFilterValue());
   }
   public long getAvgBytesDownAsLong() {
      return getFieldValueAsLong(AvgBytesDown, getAvgBytesDownFilterValue());
   }
   public long getAvgBytesAsLong() {
      return getFieldValueAsLong(AvgBytes, getAvgBytesFilterValue());
   }
   public long getAvgDurationAsLong() {
      return getFieldValueAsLong(AvgDuration, getAvgDurationFilterValue());
   }
   public long getNumSessionsAsLong() {
      return getFieldValueAsLong(NumSessions, getNumSessionsFilterValue());
   }
   
  // Main
   public static final String ClientType   = "ClientType";
   public static final String OS           = "OS";
   public static final String SessionState = "SessionState";
   public static final String Duration     = "Duration";
   public static final String BytesUp      = "BytesUp";
   public static final String BytesDown    = "BytesDown";
   public static final String Bytes        = "Bytes";
   
  // Summary
   public static final String AvgBytesUp   = "AvgBytesUp";
   public static final String AvgBytesDown = "AvgBytesDown";
   public static final String AvgBytes     = "AvgBytes";
   public static final String AvgDuration  = "AvgDuration";
   public static final String NumSessions  = "NumSessions";
    
  // Record
   public static final String StartTime    = "StartTime";
   public static final String EndTime      = "EndTime";
   
  // include field
   protected boolean clienttype    = false;
   protected boolean os            = false;
   protected boolean sessionstate  = true;
   protected boolean duration      = true;
   protected boolean bytesup       = false;
   protected boolean bytesdown     = false;
   protected boolean bytes         = true;
   
   protected boolean avgduration   = true;
   protected boolean avgbytesup    = false;
   protected boolean avgbytesdown  = false;
   protected boolean avgbytes      = true;
   protected boolean numsessions   = true;
   
   protected boolean starttime     = true;
   protected boolean endtime       = false;
   
  // include filter
   protected boolean clienttypeF   = false;
   protected boolean osF           = false;
   protected boolean sessionstateF = false;
   protected boolean durationF     = false;
   protected boolean bytesupF      = false;
   protected boolean bytesdownF    = false;
   protected boolean bytesF        = false;
   
   protected boolean avgdurationF  = false;
   protected boolean avgbytesupF   = false;
   protected boolean avgbytesdownF = false;
   protected boolean avgbytesF     = false;
   protected boolean numsessionsF = false;
   
  // Filter Value
   protected String clienttypeFV   = "";
   protected String osFV           = "";
   protected String sessionstateFV = "";
   
   protected String durationFV     = "";
   protected String bytesupFV      = "";
   protected String bytesdownFV    = "";
   protected String bytesFV        = "";
   
   protected String avgdurationFV  = "";
   protected String avgbytesupFV   = "";
   protected String avgbytesdownFV = "";
   protected String avgbytesFV     = "";
   protected String numsessionsFV  = "";
   
  // Filter Modifier
   protected String durationFG     = "ge";
   protected String bytesupFG      = "ge";
   protected String bytesdownFG    = "ge";
   protected String bytesFG        = "ge";
   
   protected String avgdurationFG  = "ge";
   protected String avgbytesupFG   = "ge";
   protected String avgbytesdownFG = "ge";
   protected String avgbytesFG     = "ge";
   protected String numsessionsFG  = "ge";
   
  //
  // -- Setters and Getters
  //
  
   public boolean getClientType() { return clienttype; }
   public void    setClientType(boolean v) { clienttype = v; }
   
   public boolean getOs() { return os; }
   public void    setOs(boolean v) { os = v; }
   
   public boolean getSessionState() { return sessionstate; }
   public void    setSessionState(boolean v) { sessionstate = v; }
   
   public boolean getDuration() { return duration; }
   public void    setDuration(boolean v) { duration = v; }
   
   public boolean getBytesUp() { return bytesup; }
   public void    setBytesUp(boolean v) { bytesup = v; }
   
   public boolean getBytesDown() { return bytesdown; }
   public void    setBytesDown(boolean v) { bytesdown = v; }
   
   public boolean getBytes() { return bytes; }
   public void    setBytes(boolean v) { bytes = v; }
   
   public boolean getAvgDuration() { return avgduration; }
   public void    setAvgDuration(boolean v) { avgduration = v; }
   
   public boolean getAvgBytesUp() { return avgbytesup; }
   public void    setAvgBytesUp(boolean v) { avgbytesup = v; }
   
   public boolean getAvgBytesDown() { return avgbytesdown; }
   public void    setAvgBytesDown(boolean v) { avgbytesdown = v; }
   
   public boolean getAvgBytes() { return avgbytes; }
   public void    setAvgBytes(boolean v) { avgbytes = v; }
   
   public boolean getNumSessions() { return numsessions; }
   public void    setNumSessions(boolean v) { numsessions = v; }
   
   public boolean getStartTime() { return starttime; }
   public void    setStartTime(boolean v) { starttime = v; }
   
   public boolean getEndTime() { return endtime; }
   public void    setEndTime(boolean v) { endtime = v; }
   
  // include filter
   public boolean getClientTypeFilter() { return clienttypeF; }
   public void    setClientTypeFilter(boolean v) { clienttypeF = v; }
   
   public boolean getOsFilter() { return osF; }
   public void    setOsFilter(boolean v) { osF = v; }
   
   public boolean getSessionStateFilter() { return sessionstateF; }
   public void    setSessionStateFilter(boolean v) { sessionstateF = v; }
   
   public boolean getDurationFilter() { return durationF; }
   public void    setDurationFilter(boolean v) { durationF = v; }
   
   public boolean getBytesUpFilter() { return bytesupF; }
   public void    setBytesUpFilter(boolean v) { bytesupF = v; }
   
   public boolean getBytesDownFilter() { return bytesdownF; }
   public void    setBytesDownFilter(boolean v) { bytesdownF = v; }
   
   public boolean getBytesFilter() { return bytesF; }
   public void    setBytesFilter(boolean v) { bytesF = v; }
   
   public boolean getAvgDurationFilter() { return avgdurationF; }
   public void    setAvgDurationFilter(boolean v) { avgdurationF = v; }
   
   public boolean getAvgBytesUpFilter() { return avgbytesupF; }
   public void    setAvgBytesUpFilter(boolean v) { avgbytesupF = v; }
   
   public boolean getAvgBytesDownFilter() { return avgbytesdownF; }
   public void    setAvgBytesDownFilter(boolean v) { avgbytesdownF = v; }
   
   public boolean getAvgBytesFilter() { return avgbytesF; }
   public void    setAvgBytesFilter(boolean v) { avgbytesF = v; }
   
   public boolean getNumSessionsFilter() { return numsessionsF; }
   public void    setNumSessionsFilter(boolean v) { numsessionsF = v; }
   
  // include filter Values
   public String getUserFilterValue()          { return userFV; }
   public void    setUserFilterValue(String v) { userFV = v;    }
   
   public String getCompanyFilterValue()          { return companyFV; }
   public void    setCompanyFilterValue(String v) { companyFV = v;    }
   
   public String getClientTypeFilterValue() { return clienttypeFV; }
   public void    setClientTypeFilterValue(String v) { clienttypeFV = v; }
   
   public String getOsFilterValue() { return osFV; }
   public void    setOsFilterValue(String v) { osFV = v; }
   
   public String getSessionStateFilterValue() { return sessionstateFV; }
   public void    setSessionStateFilterValue(String v) { sessionstateFV = v; }
   
   public String getDurationFilterValue() { return durationFV; }
   public void    setDurationFilterValue(String v) { durationFV = v; }
   
   public String getBytesUpFilterValue() { return bytesupFV; }
   public void    setBytesUpFilterValue(String v) { bytesupFV = v; }
   
   public String getBytesDownFilterValue() { return bytesdownFV; }
   public void    setBytesDownFilterValue(String v) { bytesdownFV = v; }
   
   public String getBytesFilterValue() { return bytesFV; }
   public void    setBytesFilterValue(String v) { bytesFV = v; }
   
   public String getAvgDurationFilterValue() { return avgdurationFV; }
   public void    setAvgDurationFilterValue(String v) { avgdurationFV = v; }
   
   public String getAvgBytesUpFilterValue() { return avgbytesupFV; }
   public void    setAvgBytesUpFilterValue(String v) { avgbytesupFV = v; }
   
   public String getAvgBytesDownFilterValue() { return avgbytesdownFV; }
   public void    setAvgBytesDownFilterValue(String v) { avgbytesdownFV = v; }
   
   public String getAvgBytesFilterValue() { return avgbytesFV; }
   public void    setAvgBytesFilterValue(String v) { avgbytesFV = v; }
   
   public String getNumSessionsFilterValue() { return numsessionsFV; }
   public void    setNumSessionsFilterValue(String v) { numsessionsFV = v; }
   
  // include filter modifiers
   
   public String  getDurationFilterModifier() { return durationFG; }
   public void    setDurationFilterModifier(String v) { durationFG = v; }
   
   public String  getBytesUpFilterModifier() { return bytesupFG; }
   public void    setBytesUpFilterModifier(String v) { bytesupFG = v; }
   
   public String  getBytesDownFilterModifier() { return bytesdownFG; }
   public void    setBytesDownFilterModifier(String v) { bytesdownFG = v; }
   
   public String  getBytesFilterModifier() { return bytesFG; }
   public void    setBytesFilterModifier(String v) { bytesFG = v; }
   
   public String  getAvgDurationFilterModifier() { return avgdurationFG; }
   public void    setAvgDurationFilterModifier(String v) { avgdurationFG = v; }
   
   public String  getAvgBytesUpFilterModifier() { return avgbytesupFG; }
   public void    setAvgBytesUpFilterModifier(String v) { avgbytesupFG = v; }
   
   public String  getAvgBytesDownFilterModifier() { return avgbytesdownFG; }
   public void    setAvgBytesDownFilterModifier(String v) { avgbytesdownFG = v; }
   
   public String  getAvgBytesFilterModifier() { return avgbytesFG; }
   public void    setAvgBytesFilterModifier(String v) { avgbytesFG = v; }
   
   public String  getNumSessionsFilterModifier() { return numsessionsFG; }
   public void    setNumSessionsFilterModifier(String v) { numsessionsFG = v; }
         

   public boolean modifyField(String fieldname, String value) {
   
      boolean ret = super.modifyField(fieldname, value);
      if (ret) return ret;
      
      ret = true;
      if (fieldname.equalsIgnoreCase(ClientType)) {
         setClientTypeFilter(true);
         setClientTypeFilterValue("\"" + value + "\"");
      } else if (fieldname.equalsIgnoreCase(OS)) {
         setOsFilter(true);
         setOsFilterValue("\"" + value + "\"");
      } else if (fieldname.equalsIgnoreCase(SessionState)) {
         setSessionStateFilter(true);
         setSessionStateFilterValue(value);
      } else {
         ret = false;
      }
      return ret;
   }
         
   public void reset(boolean toDefault) {
   
      if (toDefault) firsttime = true;

      super.reset(toDefault);
      
      setClientType(false);
      setOs(false);
      setSessionState(false);
      setDuration(false);
      setBytesUp(false);
      setBytesDown(false);
      setBytes(false);
      setAvgDuration(false);
      setAvgBytesUp(false);
      setAvgBytesDown(false);
      setAvgBytes(false);
      setNumSessions(false);
      setStartTime(false);
      setEndTime(false);
      
      setClientTypeFilter(false);
      setOsFilter(false);
      setSessionStateFilter(false);
      setDurationFilter(false);
      setBytesUpFilter(false);
      setBytesDownFilter(false);
      setBytesFilter(false);
      setAvgDurationFilter(false);
      setAvgBytesUpFilter(false);
      setAvgBytesDownFilter(false);
      setAvgBytesFilter(false);
      setNumSessionsFilter(false);
      
      setClientTypeFilterValue("");
      setOsFilterValue("");
      setSessionStateFilterValue("");
      setDurationFilterValue("10:00");
      setBytesUpFilterValue("1000");
      setBytesDownFilterValue("1000");
      setBytesFilterValue("1000");
      setAvgDurationFilterValue("10:00");
      setAvgBytesUpFilterValue("1000");
      setAvgBytesDownFilterValue("1000");
      setAvgBytesFilterValue("1000");
      setNumSessionsFilterValue("10");
      setDurationFilterModifier("ge");
      setBytesUpFilterModifier("ge");
      setBytesDownFilterModifier("ge");
      setBytesFilterModifier("ge");
      setAvgDurationFilterModifier("ge");
      setAvgBytesUpFilterModifier("ge");
      setAvgBytesDownFilterModifier("ge");
      setAvgBytesFilterModifier("ge");
      setNumSessionsFilterModifier("ge");
      
      if (firsttime) {
         firsttime = false;
         setSessionState(true);
         setDuration(true);
         setBytes(true);
         setAvgDuration(true);
         setAvgBytes(true);
         setNumSessions(true);
         setStartTime(true);
      }
   }
   
   static public long parseDuration(String s) throws Exception {
   
      long ret = 0;
      
     // Seconds
      int idx = s.lastIndexOf(':');
      if (idx >= 0) {
         ret += Long.parseLong(s.substring(idx+1).trim());
         s = s.substring(0, idx).trim();
      } else {
         throw new Exception("Must have at least :seconds specified");
      }
      
     // Minutes
      idx = s.lastIndexOf(':');
      int mult = 60;
      if (idx >= 0) {
         ret += Long.parseLong(s.substring(idx+1).trim()) * mult;
         s = s.substring(0, idx).trim();
         
        // Hours
         idx = s.lastIndexOf(':');
         mult = 60*60;
         if (idx >= 0) {
            ret += Long.parseLong(s.substring(idx+1).trim()) * mult;
            s = s.substring(0, idx).trim();
            
           // Days
            idx = s.lastIndexOf(':');
            mult = 24*60*60;
            if (idx >= 0) {
               ret += Long.parseLong(s.substring(idx+1).trim()) * mult;
               s = s.substring(0, idx).trim();
            } else if (s.length() > 0) {
               ret += Long.parseLong(s.trim()) * mult;
            }
            
         } else if (s.length() > 0) {
            ret += Long.parseLong(s.trim()) * mult;
         }
         
      } else if (s.length() > 0) {
         ret += Long.parseLong(s.trim()) * mult;
      }
      
      return ret;
   }
   
   static public String getDuration(long totdelt) {
   
      boolean sign = totdelt < 0;
      if (sign) totdelt = -totdelt;   
      
      long tdays = totdelt/86400;
      totdelt   -= tdays *86400;
      long thr   = totdelt/3600;
      totdelt   -= thr * 3600;
      long tmin  = totdelt/60;
      totdelt   -= tmin * 60;
      long tsec  = totdelt;

      String tdaysS =               ("") + tdays;
      String thrS   = ((thr  < 10)?"0":"") + thr;
      String tminS  = ((tmin < 10)?"0":"") + tmin;
      String tsecS  = ((tsec < 10)?"0":"") + tsec;
      
      String tdelt = "";
      if (tdays > 0)                        tdelt += tdaysS + ":";
     //if (thr   > 0 || tdelt.length() != 0) 
      tdelt += thrS + ":";
     //if (tmin  > 0 || tdelt.length() != 0) 
      tdelt += tminS;
      tdelt += ":" + tsecS;
      if (sign) tdelt = "- " + tdelt;
      return tdelt;
   }
   
   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
      ActionErrors ret = super.validate(mapping, request);
      
      String s = getClientTypeFilterValue();
      if (getClientTypeFilter() && (s == null || s.trim().length() == 0)) {
         ret.add("clientType", new ActionError("error.clienttypefilter"));
      }
      s = getOsFilterValue();
      if (getOsFilter() && (s == null || s.trim().length() == 0)) {
         ret.add("os", new ActionError("error.osfilter"));
      }
      s = getSessionStateFilterValue();
      if (getSessionStateFilter() && (s == null || s.trim().length() == 0)) {
         ret.add("sessionState", new ActionError("error.sessionstatefilter"));
      }
      s = getDurationFilterValue();
      if (getDurationFilter()) {
         try { 
            parseDuration(s); 
         } catch(Exception e) {
            ret.add("duration", new ActionError("error.durationfilter"));
         } 
      }
      s = getAvgDurationFilterValue();
      if (getAvgDurationFilter()) {
         try { 
            parseDuration(s); 
         } catch(Exception e) {
            ret.add("duration", new ActionError("error.avgdurationfilter"));
         } 
      }
      s = getBytesUpFilterValue();
      if (getBytesUpFilter()) {
         try { 
            Long.parseLong(s);
         } catch(Exception e) {
            ret.add("bytesUp", new ActionError("error.bytesupfilter"));
         } 
      }
      s = getBytesDownFilterValue();
      if (getBytesDownFilter()) {
         try { 
            Long.parseLong(s);
         } catch(Exception e) {
            ret.add("bytesDown", new ActionError("error.bytesdownfilter"));
         } 
      }
      s = getBytesFilterValue();
      if (getBytesFilter()) {
         try { 
            Long.parseLong(s);
         } catch(Exception e) {
            ret.add("bytes", new ActionError("error.bytesfilter"));
         } 
      }
      s = getAvgBytesUpFilterValue();
      if (getAvgBytesUpFilter()) {
         try { 
            Long.parseLong(s);
         } catch(Exception e) {
            ret.add("avgBytesUp", new ActionError("error.avgbytesupfilter"));
         } 
      }
      s = getAvgBytesDownFilterValue();
      if (getAvgBytesDownFilter()) {
         try { 
            Long.parseLong(s);
         } catch(Exception e) {
            ret.add("avgBytesDown", new ActionError("error.avgbytesdownfilter"));
         } 
      }
      s = getAvgBytesFilterValue();
      if (getAvgBytesFilter()) {
         try { 
            Long.parseLong(s);
         } catch(Exception e) {
            ret.add("avgBytes", new ActionError("error.avgbytesfilter"));
         } 
      }
      s = getNumSessionsFilterValue();
      if (getNumSessionsFilter()) {
         try { 
            Long.parseLong(s);
         } catch(Exception e) {
            ret.add("numSessions", new ActionError("error.numsessionsfilter"));
         } 
      }
      
      return ret;
   }
   
  // If a filter name has a text field set but NO gval, its considered simple
   public static boolean fieldHasSimpleFilter(String name) {
      boolean ret = BaseForm.fieldHasSimpleFilter(name);
      if (!ret && (name.equals(ClientType) || name.equals(OS) || name.equals(SessionState))) {
         ret = true;
      }
      return ret;
   }
   
}
