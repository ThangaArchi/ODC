package oem.edge.ed.odc.tunnel.common;

import org.apache.log4j.Priority;
import org.apache.log4j.Logger;

public class Debug4j extends DebugPrintLocal {

   private Logger cat = null;
   
   public Debug4j() {
      cat=Logger.getLogger(DebugPrint.class);
   }   
   
   private Priority getPriority(int lev) {
      Priority ret = Priority.DEBUG;
      if      (lev < DebugPrint.WARN)  ret = Priority.ERROR;
      else if (lev < DebugPrint.INFO)  ret = Priority.WARN;
      else if (lev < DebugPrint.DEBUG) ret = Priority.INFO;
      return ret;
   }
   
  // Intended to be called to reload log4j props
   public void refresh() { 
      System.out.println("Debug4j: Refresh. Not implemented!!!");
   }
   
   public int getLevel() {
      return level;
   }   
   public void setLevel(int d) {
      level = d;
      cat.setPriority(getPriority(d));
   }   
   public void println(int lev, String s) {
      if (level >= DebugPrint.DEBUG) {
         printlnd(lev, s);
      } else if (lev <= level) {
         cat.log(getPriority(lev), s);
      }
   }
   public void println(String s) {
      if (level >= DebugPrint.DEBUG) {
         printlnd(s);
      } else if (DebugPrint.INFO <= level) {
         cat.log(getPriority(DebugPrint.INFO), s);
      }
   }
   public void println(int lev, Throwable e) {
      if (level >= DebugPrint.DEBUG) {
         printlnd(lev, e);
      } else if (lev <= level) {
         cat.log(getPriority(lev), "", e);
      }
   }
   public void printlnd(int lev, String s) {
      if (lev <= level) {
         String tinfo = " ";
         if (level >= DebugPrint.DEBUG2) {
            tinfo = " : " + Thread.currentThread().toString() + " : ";
         }
      
         cat.log(getPriority(lev), DebugPrint.getFormattedDate() + tinfo + s);
      }
   }
   public void printlnd(String s) {
      if (DebugPrint.INFO <= level) {
         cat.log(getPriority(DebugPrint.INFO), 
                 DebugPrint.getFormattedDate() + " " + s);
      }
   }
   public void printlnd(int lev, Throwable e) {
      if (lev <= level) {
         String tinfo = " ";
         if (level >= DebugPrint.DEBUG2) {
            tinfo = " : " + Thread.currentThread().toString() + " : ";
         }
         cat.log(getPriority(lev), DebugPrint.getFormattedDate() + tinfo, e);
      }
   }
}
