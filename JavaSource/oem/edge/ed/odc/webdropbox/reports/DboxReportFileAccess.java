package oem.edge.ed.odc.webdropbox.reports;

import org.apache.struts.action.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.*;
import java.text.*;
import java.lang.*;

public class DboxReportFileAccess {

   String fileName = "";
   String user = "";
   String state = "";
   String created = "";
   String transferRate = "";
   
   public DboxReportFileAccess() {
   }
   
   public String getAccessFileName()         { return fileName; }
   public void   setAccessFileName(String v) { fileName = v;    }
   
   public String getAccessName()         { return user; }
   public void   setAccessName(String v) { user = v;    }
   
   public String getAccessState()         { return state; }
   public void   setAccessState(String v) { state = v;    }
   
   public String getAccessCreated()         { return created; }
   public void   setAccessCreated(String v) { created = v;    }
   
   public String getAccessTransferRate()         { return transferRate; }
   public void   setAccessTransferRate(String v) { transferRate = v;    }
}
