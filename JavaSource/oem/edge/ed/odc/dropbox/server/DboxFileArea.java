package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.util.*;

import java.lang.*;
import java.io.*;
import java.util.*;

public class DboxFileArea {

   final public byte FSTYPE_JFS        = 1;
   final public byte FSTYPE_AFS        = 2;
   
   final public byte STATE_UNKNOWN     = 1;
   final public byte STATE_NORMAL      = 2;
   final public byte STATE_LOW         = 3;
   final public byte STATE_BROKEN      = 4;

   protected String filearea           = null;
   protected long total                = 0;
   protected long used                 = 0;
   
   protected int priority              = 0;
   
   protected byte fstype               = FSTYPE_JFS;
   protected byte state                = STATE_UNKNOWN;
   
   protected long compallocated        = 0;
   
   protected String lastAccessed       = null;
   protected String getLastAccessed() {
      return lastAccessed;
   }
   protected void   setLastAccessed(String s) {
      lastAccessed = s;
   }
   
   public DboxFileArea(String a, long tot, long used) {
      this.filearea = a;
      this.total = tot;
      this.used = used;
   }
   
   public int  getPriority()       { return priority; }
   public void setPriority(int v)  { priority = v;    }
   
   public long getComponentAllocation()        { return compallocated; }
   public void setComponentAllocation(long v)  { compallocated = v;    }
   
   public byte getFSType()       { return fstype; }
   public void setFSType(byte v) { fstype = v;    }
   
   public byte getState()       { return state; }
   public void setState(byte v) { state = v;    }
   
   public long spaceLeft()      { return total-used; }
   
   public int percentUsed()     { 
      return (int)((used*100)/(total > 0?total:1));
   }
   
   public long getUsed()         { return used;  }
   public long getTotal()        { return total; }
   
   public String getTopLevelDirectory() { return filearea; }
   
   public void returnSpace(long space) {
      System.out.println("returnSpace: " + filearea + " " + space);
      used -= space;
      if (used < 0) {
         System.out.println("DboxFileArea: returnSpace: returing " + space +
                            " causes negative space value = " + used);
         used = 0;
      }
   }
   
   public synchronized void allocateSpace(long space) throws DboxException {
      
      System.out.println("returnSpace: " + filearea + " " + space);
      if (total <= 0 || total-used < space) {
         throw new DboxException("DboxFileArea:>> Out of Space: " + filearea +
                                 " total = " + total + " used = " + used +
                                 " left = " + (total - used), 1);
      }
      
      used -= space;
   }
   
   public void update() {
      if (fstype == FSTYPE_JFS) {
         ;
      }
   }
   
   public String toString() {
      return "DboxFileArea:\n" + Nester.nest(filearea + "\n") +
             Nester.nest(" total = " + total + " used = " + used +
                         " left = " + (total - used)) + 
             Nester.nest(" FSType = " + fstype + " state = " + state + 
                         " prior = " + priority);
   }
}
