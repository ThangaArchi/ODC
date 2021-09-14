package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.util.*;

import java.lang.*;
import java.io.*;
import java.util.*;

public class DboxFileAclInfo extends AclInfo {
   long fileid;
   public DboxFileAclInfo(DboxFileAclInfo d) {
      super(d);
      fileid = d.fileid;
   }
   public DboxFileAclInfo(AclInfo d, long fileid) {
      super(d);
      this.fileid = fileid;
   }
      
   public long getFileId()       { return fileid; }
   public void setFileId(long v) { fileid = v;    }
      
   public String toString() {
      return super.toString() + 
         "\n -- DboxFileAclInfo --\n" + Nester.nest("fileid = " + fileid);
   }
}
