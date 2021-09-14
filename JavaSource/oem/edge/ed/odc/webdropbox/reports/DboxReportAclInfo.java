package oem.edge.ed.odc.webdropbox.reports;

import org.apache.struts.action.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.*;
import java.text.*;
import java.lang.*;

public class DboxReportAclInfo {

   String aclname = "";
   String acltype = "";
   String created = "";
   
   public DboxReportAclInfo() {
   }
   
   public String getAclName()         { return aclname; }
   public void   setAclName(String v) { aclname = v;    }
   
   public String getAclType()         { return acltype; }
   public void   setAclType(String v) { acltype = v;    }
   
   public String getAclCreated()         { return created; }
   public void   setAclCreated(String v) { created = v;    }
}
