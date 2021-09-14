package oem.edge.ed.odc.webdropbox.reports;

import org.apache.struts.action.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.*;
import java.text.*;
import java.lang.*;

public class DboxReportFileInfo {

   String fileId = "";
   String fileName = "";
   String size = "";
   String state = "";
   String md5 = "";
   String numcomponents = "";
   String xferrate = "";
   String created = "";
   String deleted = "";
   
   Vector accessed = new Vector();
   Vector packages = null;
   
   public DboxReportFileInfo() {
   }
   
   public Enumeration getAccess() { return accessed.elements(); }
   
   public void addAccessInfo(DboxReportFileAccess a) {
      accessed.addElement(a);
   }
   
   public synchronized void addPackage(DboxReportPackageInfo pinfo) {
      if (packages == null) packages = new Vector();
      packages.addElement(pinfo);
   }
   
   public synchronized Enumeration getPackages() { 
      return packages == null ? new Vector().elements() : packages.elements(); 
   }
   
   public String getFileId()         { return fileId; }
   public void   setFileId(String v) { fileId = v;    }
   
   public String getFileName()         { return fileName; }
   public void   setFileName(String v) { fileName = v;    }
   
   public String getFileMD5()         { return md5; }
   public void   setFileMD5(String v) { md5 = v;    }
   
   public String getFileSize()         { return size; }
   public void   setFileSize(String v) { size = v;    }
   
   public String getFileState()         { return state; }
   public void   setFileState(String v) { state = v;    }
   
   public String getFileTransferRate()         { return xferrate; }
   public void   setFileTransferRate(String v) { xferrate = v;    }
   
   public String getFileNumComponents()         { return numcomponents; }
   public void   setFileNumComponents(String v) { numcomponents = v;    }
   
   public String getFileCreated()         { return created; }
   public void   setFileCreated(String v) { created = v;    }
   
   public String getFileDeleted()         { return deleted; }
   public void   setFileDeleted(String v) { deleted = v;    }
}
