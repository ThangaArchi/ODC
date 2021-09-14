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

public class DboxReportPackageInfo {

   String  packageId = "";
   String  packageName = "";
   String  owner = "";
   String  company = "";
   String  size = "";
   String  state = "";
   String  numfiles = "";
   String  xferrate = "";
   String  created = "";
   String  committed = "";
   String  deleted = "";
   String  desc = "";
   String  itarstatus = "";
   
   boolean islimited = false;
   
   Vector files   = new Vector();
   Vector aclinfo = new Vector();
   Vector accinfo = new Vector();

   public DboxReportPackageInfo() {
   }
   
   public void addFile(DboxReportFileInfo file) {
      files.addElement(file);
     // If a file is added which is Failed, the package is failed as well
      if (file.getFileState().equals("Failed")) {
         setPackageState("Failed");
      }
   }
   
   public DboxReportFileInfo getFileById(String fileid) {
      DboxReportFileInfo ret = null;
      if (files.size() > 0) {
         Enumeration enum = files.elements();
         while(enum.hasMoreElements()) {
            DboxReportFileInfo finfo = (DboxReportFileInfo)enum.nextElement();
            if (finfo.getFileId().equals(fileid)) {
               ret = finfo;
               break;
            }
         }
      }
      return ret;
   }
   
   public DboxReportFileInfo getFileByName(String filename) {
      DboxReportFileInfo ret = null;
      if (files.size() > 0) {
         Enumeration enum = files.elements();
         while(enum.hasMoreElements()) {
            DboxReportFileInfo finfo = (DboxReportFileInfo)enum.nextElement();
            if (finfo.getFileName().equals(filename)) {
               ret = finfo;
               break;
            }
         }
      }
      return ret;
   }
   
   public void addAclInfo(DboxReportAclInfo a) {
      aclinfo.addElement(a);
   }
   public void addAccessInfo(DboxReportPackageAccess a) {
      accinfo.addElement(a);
   }
   
   public Enumeration getFiles()      { return files.elements(); }
   public Enumeration getAclInfo()    { return aclinfo.elements(); }
   public Enumeration getAccessInfo() { return accinfo.elements(); }
   
   public boolean isLimited()           { return islimited; }
   public void    setLimited(boolean v) { islimited = v;    }
   
   public String getPackageId()         { return packageId; }
   public void   setPackageId(String v) { packageId = v;    }
   
   public String getPackageDescription()         { return desc; }
   public void   setPackageDescription(String v) { desc = v;    }
   
   public String getPackageItarStatus()         { return itarstatus; }
   public void   setPackageItarStatus(String v) { itarstatus = v;    }
   
   public String getPackageName()         { return packageName; }
   public void   setPackageName(String v) { packageName = v;    }
   
   public String getPackageOwner()         { return owner; }
   public void   setPackageOwner(String v) { owner = v;    }
   
   public String getPackageCompany()         { return company; }
   public void   setPackageCompany(String v) { company = v;    }
   
   public String getPackageSize()         { return size; }
   public void   setPackageSize(String v) { size = v;    }
   
   public String getPackageState()         { return state; }
   public void   setPackageState(String v) { state = v;    }
   
   public String getPackageNumFiles()         { return numfiles; }
   public void   setPackageNumFiles(String v) { numfiles = v;    }
   
   public String getPackageTransferRate()         { return xferrate; }
   public void   setPackageTransferRate(String v) { xferrate = v;    }
   
   public String getPackageCreated()         { return created; }
   public void   setPackageCreated(String v) { created = v;    }
   
   public String getPackageCommitted()         { return committed; }
   public void   setPackageCommitted(String v) { committed = v;    }
   
   public String getPackageDeleted()         { return deleted; }
   public void   setPackageDeleted(String v) { deleted = v;    }
}
