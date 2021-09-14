package oem.edge.ed.odc.dsmp.server;

import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.util.*;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.log4j.Logger;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004,2005,2006                           */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rigts                                 */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */

public class DboxAlert {
   public static final int SEV1 = 1;
   public static final int SEV2 = 2;
   public static final int SEV3 = 3;
   public static final int SEV4 = 4;
   
   private static Logger log = Logger.getLogger(DboxAlert.class.getName());
   
  // This is the global alertinfo. If there is threadspecific alert info, 
  //  use that instead
   public static String alertinfo="NotSet";
   protected static 
   java.lang.ThreadLocal thread_alertinfo = new java.lang.ThreadLocal();
   
   public static void setAlertInfo(String i) {
      thread_alertinfo.set(i);
   }
   
   public static String getAlertInfo() {
      Object ret = thread_alertinfo.get();
      if (ret == null || ! (ret instanceof String)) {
         ret = alertinfo;
      }
      return (String)ret;
   }
   
   public static String getStackTrace(Throwable e) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
      PrintStream eout = new PrintStream(baos,true);
      e.printStackTrace(eout);
      eout.flush();
      return baos.toString();
   }
   
   public static void alert(Throwable e) {
      alert(SEV2, "Generic Exception Processing", 0, 
            "Exception occured, and Alert was deemed appropriate\n\n" +
            "AlertInfo:\n" + Nester.nest(getAlertInfo(),5) + "\n\n" +
            "Exception:\n" + Nester.nest(getStackTrace(e),5));
   }
   
   public static void alert(int sev, String title, int code, String desc) {
      alert(sev, title, code, desc, null);
   }
   
   public static void alert(int sev, String title, 
                            int code, String desc, 
                            Throwable except) {
                            
      String host = "unknownhost";
      try {
         host = InetAddress.getLocalHost().getHostName();
      } catch(Exception ee) {
      }
      
      String alertstring = 
         "!!ALERT!! - SEV"   + sev + 
         "\n      Host = "  + host +
         "\n     Title = "  + title +
         "\n      code = "  + code  + 
         "\n AlertInfo :\n" + Nester.nest(getAlertInfo(),5) +
         "\n      desc :\n" + Nester.nest(desc, 5);
         
      if (except != null) {
         alertstring = alertstring + "\n\n Exception:\n" + 
            Nester.nest(getStackTrace(except), 5);
      }
         
     /*
      try {
         dce_moniter_alert("Dropbox", "ServiceDown", host, alertstring);
      } catch(Exception ee) {}
     */
      
      log.error(alertstring);
   }
   
  //********************************************************************
  /**
   * dce_moniter_alert
   *
   */
//dce_moniter_alert("Web_Server", "ServiceUp", hostnamePort, "WAS_Servlet_Up");
//dce_moniter_alert("Web_Server", "ServiceDown", hostnamePort, "WAS_Servlet_Down");  
   static boolean monitordebug = false;
   static void dce_moniter_alert(String monitor,
                                 String event,
                                 String object,
                                 String data) {
      
      String dce_monitor_file = "/var/local/etc/dce_monitor";
     
      String cmd[] = {
         dce_monitor_file,
         "-m",  monitor,
         "-e",  event,
         "-o",  object,
         "-d",  data
      };
     
      try {
        
         File f = new File(dce_monitor_file);
         if (f.exists()) {
            Process p = Runtime.getRuntime().exec(cmd);
           
            if (monitordebug) {
               String TmpStr1 = "";
               for(int count = 0; count < cmd.length; count++) {
                  TmpStr1 = TmpStr1.concat( (cmd[count] + " ") );
               }
               System.out.println("Issued dce_monitor alert" );
               System.out.println("dce_monitor cmd was:" + TmpStr1);
            } //= end if debug
         } //- end if (f.exists(
        
         else {
            System.err.print("ADV_dcsldap: dce_moniter_alert: Error!");
            System.err.print(" file:" + cmd[0] + " does NOT exist");
            System.err.print(" monitor=" + monitor +
                             " event=" + event +
                            
                             " object=" + object +
                             " data=" + data);
         }
      } catch (Exception e) {
        //what should we do here?? pjs
         e.printStackTrace();
      }
      return;
   }

}
