package oem.edge.ed.odc.meeting.client;

import  oem.edge.ed.odc.meeting.common.*;
import  oem.edge.ed.odc.dsmp.common.*;

import java.util.*;
import java.lang.*;

public class DScraper extends BScraper {

   String libname = "XScraper";
   
   public DScraper(String suffix) {
      super(suffix);
   }
   
   // loadScraper() - load the library/exe to handle 'native' calls
   protected synchronized boolean loadScraper(boolean loud) {
      synchronized(libname) {
      
         if (tryloaded) return loaded;
         
         tryloaded = true;
      
         int tries = 0;
         
         String append = "";
         
         String lastname = libname + platinfo;
         System.out.println("DSMPLoad: [" + lastname + "]");
         
         while (!loaded && tries < 2) {
         
            tries++;
            
            lastname = libname + platinfo;
            
           // Try -green on alternating runs
            if (tries == 2) {
               lastname = libname + platinfo + "-green";
            } 
            
            try {
               System.loadLibrary(lastname);
               loaded = true;
            } catch(UnsatisfiedLinkError e) {
               if (loud) {
                  System.out.println("DScraper: Could not load " + 
                                     lastname);
                  System.out.println("Exception nofile: " + e.toString());
               }
            } catch(SecurityException e) {
               if (loud) {
                  System.out.println("DScraper: Could not load " + 
                                     lastname);
                  System.out.println("Exception Security: " + e.toString());
               }
            } catch(Throwable e) {
               if (loud) {
                  System.out.println("DScraper: Could not load " + 
                                     lastname);
                  System.out.println("Exception generic: " + e.toString());
               }
            }
            if (!loaded && tries == 2 && platinfo.length() != 0) {
               platinfo = "";
               tries = 0;
            }
         }
      }
      
      return loaded;
   }
   

   
  // --------------------- Native Methods -----------------------
   
   
   public native int                connect(String host, boolean viewonly, 
                                            boolean force) 
      throws MissingDependencies, PossibleMissingDependencies;
      
   public native void               disconnect();
   
   public native int                selectWindow();
   
   protected native void            scrapingDelayI(int v);
   
   public native void               pause();
   public native void               resume();
   
   public native int                getDesktopWindow();
   
   public native boolean            isModeStillValid();
   
   public native Vector             getToplevelWindows();
   
   public native int                configureToDesktop();
   public native int                configureToWindow(int window);
   public native int                configure(int x, int y, int w, int h);
   
   public native String             getWindowTitle(int window);
   
   public native java.awt.Point     getCursorPosition();
   
   public native java.awt.Rectangle getCurrentFrame();
   
   public native java.awt.Rectangle getNewFrame(boolean forceupdate);
   public native java.awt.Rectangle replayLastFrame();
   
   public native int[]              getUpdatedPixels(int a[]);
   public native byte[]             getUpdatedPixelsInBytes(byte a[]);
   public native byte[]             getUpdatedPixelsInBytes2(byte a[]);
   
   public native void               injectKey(java.awt.Point p, 
                                              boolean keyPress, 
                                              boolean keyCodeOrChar,
                                              int     keysym);
   public native void               injectMouse(java.awt.Point p, 
                                                boolean buttonPress, 
                                                int     buttonNumber);
}
