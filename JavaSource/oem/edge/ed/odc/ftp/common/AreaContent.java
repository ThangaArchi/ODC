package oem.edge.ed.odc.ftp.common;

import  oem.edge.ed.odc.dsmp.common.*;

import java.io.File;
import java.util.Date;
public class AreaContent {
   protected String area;
   protected String name;
   protected byte type;
   protected long size;
   protected long timedate;
   
   public static final byte TYPE_FILE      = (byte)1;
   public static final byte TYPE_DIRECTORY = (byte)2;
   public static final byte TYPE_UNKNOWN   = (byte)127;
   
   public AreaContent() {
      area = null;
      name = null;
      type = 0;
      size = -1;
      timedate = 0;
   }
   public AreaContent(String area, String name, byte type, 
                      long size, long timedate) {
      this.area = area;
      this.name = name;
      this.type = type;
      this.size = size;
      this.timedate = timedate;
   }
   
   public AreaContent(File f) {
      name = f.getName();
      area = f.getParent();
      if (area == null) {
         area = "";
      }
      type = (byte)(f.isDirectory()?2:1);
      size = f.length();
      timedate = f.lastModified();
   }
   
   public String getArea()     { return area;     }
   public String getName()     { return name;     }
   public byte   getType()     { return type;     }
   public long   getSize()     { return size;     }
   public long   getTimeDate() { return timedate; }
   
   public void   setArea(String   v) { area     = v; }
   public void   setName(String   v) { name     = v; }
   public void   setType(byte     v) { type     = v; }
   public void   setSize(long     v) { size     = v; }
   public void   setTimeDate(long v) { timedate = v; }
   
   public String toString() {
      String ret = "AreaContent:" +
                   "\n\t\t area[" + area +
                   "]\n\t\t name[" + name +
                   "]\n\t\t type[" + type +
                   "]\n\t\t size[" + size +
                   "]\n\t\t time[" + (new Date(timedate).toString()) + "]";
      return ret;
   }
}
