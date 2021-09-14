package oem.edge.ed.odc.webdropbox.reports;

import org.apache.struts.action.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.*;
import java.text.*;
import java.lang.*;

public class DboxReportPackageAccess {

   String user  = "";
   String state = "";
   String last  = "";
   String rate  = "";
   
   public DboxReportPackageAccess() {
   }
   
   public String getAccessName()         { return user; }
   public void   setAccessName(String v) { user = v;    }
   
   public String getAccessState()         { return state; }
   public void   setAccessState(String v) { state = v;    }
   
   public String getAccessLast()         { return last; }
   public void   setAccessLast(String v) { last = v;    }
   
   public String getAccessTransferRate()         { return rate; }
   public void   setAccessTransferRate(String v) { rate = v;    }
}
