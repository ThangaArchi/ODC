package oem.edge.ed.odc.applet;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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

import java.applet.*;
import java.util.*;
import java.awt.*;

public class LaunchApplet extends Applet {
   
   boolean isJava1 = true;
   
   public LaunchApplet() {
      super();
      calculateJava1();
      oem.edge.ed.odc.tunnel.common.Misc.useURLConnection2 = false;
   }
   
   public void init() {
      System.out.println("LaunchApplet initing");
   }
   
   public void start() {
      if (isJava1) {
        //setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
         add(new Label("Java2 is required!"));
         add(new Label("Upgrade your browser or Java plugin installation"));
         return;
      }
   
   
      System.out.println("LaunchApplet starting");
      String args[] = new String[4];
      args[0] = "-URL http://iceland/cc";
      args[1] = "-CH_TUNNELCOMMAND XFR";
      args[2] = "-CH_NOSTREAM";
      args[3] = "-THE_END";
      
      LaunchApp lapp = new LaunchApp();
      lapp.inapplet = true;
      oem.edge.ed.odc.tunnel.common.DebugPrint.setInAnApplet(true);  
      lapp.begin(args);
   }
   
   public void stop() {
      System.out.println("LaunchApplet stopping");
   }
   public void destroy() {
      System.out.println("LaunchApplet destroying");
   }
   
// JMC - stolen from InstallAndLaunchApp
   public void calculateJava1() {
     // Determine which version of Java we are dealing with...
      String java = System.getProperty("java.version");
      System.out.println("Java version is " + java);
      
      boolean done = false;
      StringTokenizer s = new StringTokenizer(java);
      
      while (! done && s.hasMoreTokens()) {
         String t = s.nextToken();
         
         try {
            int major = 0;
            int minor = 0;
            
            int dot = t.indexOf('.');
            int dot2 = t.indexOf('.',dot+1);
            
            System.out.println("dot is " + dot + "; dot2 is " + dot2);
            major = Integer.parseInt(t.substring(0,dot).trim());
            if (dot2 != -1)
               minor = Integer.parseInt(t.substring(dot+1,dot2));
            else
               minor = Integer.parseInt(t.substring(dot+1));
            
            if (major > 1 || (major == 1 && minor > 1))
               isJava1 = false;
            
            done = true;
            System.out.println("Java version is 1? " + isJava1);
         }
         catch (Exception e) {
            e.printStackTrace();
         }
      }
   }
}
