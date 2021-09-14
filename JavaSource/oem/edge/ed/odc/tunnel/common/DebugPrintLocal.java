package oem.edge.ed.odc.tunnel.common;
import java.util.Date;

/**
 * Insert the type's description here.
 * Creation date: (8/10/00 11:30:33 AM)
 * @author: Administrator
 */
/*
import netscape.security.*;
import com.ms.security.*; 
*/
import java.io.*;
public class DebugPrintLocal implements DebugPrintI {
   
   protected int     level        = DebugPrint.INFO;
   protected boolean inapplet     = false;
   protected boolean isclientside = false;
   
  // Intended to be called to reload log4j props
   public void refresh() { 
      System.out.println("DebugPrint: Refresh. Not implemented!!!");
   }
   
   public boolean doDebug() {
      return level >= DebugPrint.DEBUG;
   }   
   
   public int getLevel() {
      return level;
   }   
   public void setLevel(int d) {
      level = d;
   }   
   public void setInAnApplet(boolean d) {
      inapplet = d;
   }   
   public boolean inAnApplet() {
      return inapplet;
   }   
   
   public void setClientSide(boolean d) {
      isclientside = d;
   }   
   public boolean getClientSide() {
      return isclientside;
   }   
   
   public void println(int lev, String s) {
      if (level >= DebugPrint.DEBUG) {
         printlnd(lev, s);
      } else if (lev <= level && DebugPrint.checkallowed()) {
         System.out.println(s);
         System.out.flush();
      }
   }
   public void println(String s) {
      if (level >= DebugPrint.DEBUG) {
         printlnd(s);
      } else if (DebugPrint.INFO <= level && DebugPrint.checkallowed()) {
         System.out.println(s);
         System.out.flush();
      }
   }
   public void println(int lev, Throwable e) {
      if (level >= DebugPrint.DEBUG) {
         printlnd(lev, e);
      } else if (lev <= level && DebugPrint.checkallowed()) {
         e.printStackTrace(System.out);
         System.out.flush();
      }
   }
   public void printlnd(int lev, String s) {
      if (lev <= level && DebugPrint.checkallowed()) {
         String tinfo = " ";
         if (level >= DebugPrint.DEBUG2) {
            tinfo = " : " + Thread.currentThread().toString() + " : ";
         }
         System.out.println(DebugPrint.getFormattedDate() + tinfo + s);
         System.out.flush();
      }
   }
   public void printlnd(String s) {
      if (DebugPrint.INFO <= level && DebugPrint.checkallowed()) {
         System.out.println(DebugPrint.getFormattedDate() + " " + s);
         System.out.flush();
      }
   }
   public void printlnd(int lev, Throwable e) {
      if (lev <= level && DebugPrint.checkallowed()) {
         String tinfo = " ";
         if (level >= DebugPrint.DEBUG2) {
            tinfo = " : " + Thread.currentThread().toString() + " : ";
         }
         System.out.print(DebugPrint.getFormattedDate() + tinfo);
         e.printStackTrace(System.out);
         System.out.flush();
      }
   }
}
