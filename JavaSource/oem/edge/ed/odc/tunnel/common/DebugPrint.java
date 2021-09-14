package oem.edge.ed.odc.tunnel.common;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.*;
import java.lang.*;
import java.io.StringWriter;

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
public class DebugPrint {

   public static final int ERROR  = 1;
   public static final int WARN   = 6;
   public static final int INFO   = 11;
   public static final int INFO2  = 12;
   public static final int INFO3  = 13;
   public static final int INFO4  = 14;
   public static final int INFO5  = 15;
   public static final int DEBUG  = 16;
   public static final int DEBUG2 = 17;
   public static final int DEBUG3 = 18;
   public static final int DEBUG4 = 19;
   public static final int DEBUG5 = 20;
   
  // Support narrowing debug messages by module
  // If moduleChecking is ON, also collect module names visited
   protected static Hashtable modulesEnabled = new Hashtable();
   protected static Hashtable modules        = new Hashtable();
   protected static boolean moduleChecking   = false;;
   
  /*
  ** Set this to TRUE and recompile to enabled realtime debug
  */
   public static final boolean isEnabled = true;
   
   static private DateFormat dateformat = null;
   
   static private DebugPrintI dp  = DebugPrint.getPrintObject();
   
   static public void setDateFormat(DateFormat df) {
      dateformat = df;
   }
   static public String getFormattedDate() {
      if (dateformat == null) {
         dateformat = new SimpleDateFormat("HH:mm:ss.SSS",java.util.Locale.US);
      }
      return dateformat.format(new Date());
   }
   
   static public DebugPrintI getPrintObject() {
      if (dp == null) {
         try {
            dp = (DebugPrintI)
               Class.forName("oem.edge.ed.odc.tunnel.common.Debug4j").newInstance();
         } catch (Throwable e) {
            dp = new DebugPrintLocal();
         }
         
         Hashtable p = System.getProperties();
         initCheckModules(p);
      }
      return dp;
   }
   
   static public void initCheckModules(Hashtable p) {
      try {
         
         String enableChecking = 
            (String)p.get("DebugPrint.moduleChecking");
            
         if (enableChecking == null) {
            enableChecking = (String)
               p.get("DebugPrint.moduleChecking".toUpperCase());
         }
                                                   
         if (enableChecking != null) {
            setModuleChecking(enableChecking.equalsIgnoreCase("TRUE")); 
         }
         
         try {
            String lev = (String)p.get("DebugPrint.level");
            if (lev == null)
               lev = (String)p.get("DebugPrint.level".toUpperCase());
               
            setLevel(Integer.parseInt(lev));
         } catch(Throwable ttt) {
         }
         
         Enumeration enum = p.keys();
         while(enum.hasMoreElements()) {
            Object nelm = enum.nextElement();
            if (nelm instanceof String) {
               String s = (String)nelm;
               if (s.indexOf("DebugPrint.enableModules") == 0 ||
                   s.indexOf("DebugPrint.enableModules".toUpperCase()) == 0) {
                  String modnames = (String)p.get(s);
                  StringTokenizer stok = new StringTokenizer(modnames, ":");
                  while(stok.hasMoreTokens()) {
                     String modname = stok.nextToken();
                     if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
                        System.out.println("DebugPrint: addModule[" + 
                                           modname + "]");
                     }
                     enableModule(modname);
                  }
               }
            }
         }
      } catch(Throwable tt) {
         tt.printStackTrace();
         System.out.println("TODO ... remove Debug Msg for lala");
      }
   }
   
   public static String urlRewrite(String url, String id) {
      String ret = null;
      int idx = url.indexOf('?');
      if (idx < 0) {
         ret = url + ";jsessionid=" + id;
      } else {
         ret = url.substring(0, idx) + ";jsessionid=" + id 
             + url.substring(idx);
      }
      return ret;
   }
   
  // Intended to be called to reload log4j props
   public static void refresh() { 
      dp.refresh();
   }
   
   public static boolean doDebug() {
      return dp.doDebug();
   }   
   
   public static int getLevel() {
      return dp.getLevel();
   }   
   public static void setLevel(int d) {
      dp.setLevel(d);
   }   
   public static void setInAnApplet(boolean d) {
      dp.setInAnApplet(d);
   }   
   public static boolean inAnApplet() {
      return dp.inAnApplet();
   }   
   
   public static void setClientSide(boolean d) {
      dp.setClientSide(d);
   }   
   public static boolean getClientSide() {
      return dp.getClientSide();
   }   
   
   public static void println(int lev, String s) {
      dp.println(lev, s);
   }
   public static void println(String s) {
      dp.println(s);
   }
   public static void println(int lev, Throwable e) {
      dp.println(lev, e);
   }
   public static void printlnd(int lev, String s) {
      dp.printlnd(lev, s);
   }
   public static void printlnd(String s) {
      dp.printlnd(s);
   }
   public static void printlnd(int lev, Throwable e) {
      dp.printlnd(lev, e);
   }
   
   public static String showbytes(byte buf[], int ofs, int len) {
      StringBuffer ret  = new StringBuffer("   0000: ");
      StringBuffer tail = new StringBuffer("  *");
      len += ofs;
      int i = ofs;
      int r = 0;
      for (; i < len; i++) {
         String v = Integer.toHexString(((int)buf[i]) & 0xff);
         if (r != 0 && (r % 16) == 0) {
            String newaddr = Integer.toHexString(r);
            while(newaddr.length() < 4) newaddr = "0"+newaddr;
            ret.append(tail.toString()).append("*\n   ")
               .append(newaddr).append(": ");
            tail = new StringBuffer("  *");
         }
         if (r != 0 && (r % 8) == 0 && (r % 16) != 0) {
            ret.append("  ");
            tail.append("|");
         }
         if (v.length() == 1) {
            ret.append(" 0");
         } else {
            ret.append(" ");
         }
         ret.append(v);
         if (buf[i] >= 32 && buf[i] <= 0x7e) {
            tail.append((char)buf[i]);
         } else {
            tail.append('.');
         }
         r++;
      }
      
      int modv = r % 16;
      while (modv != 0 && modv < 16) {
         ret.append("   ");
         tail.append(" ");
         if (modv == 8) { ret.append("  "); tail.append("|"); }
         modv++;
      }
      
      ret.append(tail.toString()).append("*");
      
      return ret.toString();
   }
   
   public static void enableModule(String m) {
      
      if (m.indexOf("!") == 0) {
         modulesEnabled.put(m.substring(1), "");
      } else {
         modulesEnabled.put(m, m);
      }
   }
   public static void disableModule(String m) {
      if (m.indexOf("!") == 0) m = m.substring(1);
      modulesEnabled.remove(m);
   }
   public static void disableAllModules() {
      modulesEnabled = new Hashtable();
   }
   public static boolean isModuleDirectlyEnabled(String m) { 
      if (m.indexOf("!") == 0) m = m.substring(1);
      String v = (String)modulesEnabled.get(m);
      return v != null && v.equals(m);
   }
   public static boolean isModuleEnabled(String m) { 
      boolean ret = false;
      
      if (m.indexOf("!") == 0) m = m.substring(1);
      
      String v = (String)modulesEnabled.get(m);
      if (v != null) {
         return v.equals(m);
      } else {
      
         int idx;
         while((idx=m.lastIndexOf('.')) > 0) {
            m=m.substring(0,idx);
            v = (String)modulesEnabled.get(m);
            if (v != null) {
               if (!v.equals(m)) return false;
               else              ret = true;
            }
         }
      }
      return ret;
   }
   
   
   public static Hashtable getModulesVisited() { 
      return (Hashtable)modules.clone();
   }
   public static Hashtable getModulesEnabled() { 
      return (Hashtable)modulesEnabled.clone();
   }
   
   public static void    setModuleChecking(boolean v) { moduleChecking=v; }
   public static boolean getModuleChecking() { return moduleChecking; }
   public static boolean checkallowed() {
      if (moduleChecking) {
        // In 1.4 timeframe, the getStackTrace is available
        //  for now, be ugly ... REAL ugly
         Exception e = new Exception();
         e.fillInStackTrace();
         StringWriter sw = new StringWriter();
         e.printStackTrace(new PrintWriter(sw, true));
         StringBuffer sb = sw.getBuffer();
         String s        = sb.toString();
         String clcm = "oem.edge.ed.odc.tunnel.common.DebugPrint.checkallowed";
         
         try {
            int idx     = s.indexOf(clcm);
         
            int  firstNL  = s.indexOf('\n');
            int  secondNL = s.indexOf('\n', firstNL+1);
            char postchar = sb.charAt(idx + clcm.length());
            if (idx <= 0 || firstNL < 0 || firstNL > idx || secondNL <= idx) {
               throw new Exception("bad fmt");
            }
            
            String prestring = s.substring(firstNL, idx);
//            System.out.println("s=\n" + s);
//            System.out.println("idx=" + idx + " NL1=" + firstNL + " NL2=" + secondNL + " postChar[" + postchar + "] prestring[" + prestring + "]");
            while(true) {
               idx        = s.indexOf(prestring, idx);
               idx       += prestring.length();
               int pcidx  = s.indexOf(postchar, idx);
               String str = s.substring(idx, pcidx);
//               System.out.println("idx=" + idx + " pcidx=" + pcidx + " str[" + postchar + "]");
               if (str.indexOf("oem.edge.ed.odc.tunnel.common.Debug") != 
                   0) {
                  if (getLevel() > 1000) System.out.println("DebugPrint: Checking allowed for [" + str + "]");
                  
                 // just keep track of everything
                  modules.put(str, str);
                  
                 // and, well, CAN I show some debug?
                  return isModuleEnabled(str);
               }
            }
            
         } catch(Exception ee) {
            setModuleChecking(false);
            DebugPrint.printlnd(DebugPrint.ERROR, 
                                "DebugPrint: Checking enabled, but can't correctly parse stacktrace! Turning checking OFF!");
            DebugPrint.println(DebugPrint.ERROR, e);
            return true;
         }
            
      }
      return true;
   }
}
