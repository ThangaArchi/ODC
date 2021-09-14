package oem.edge.ed.odc.util;
import oem.edge.ed.odc.tunnel.common.DebugPrint;
import java.util.*;
import java.lang.*;

public class TimeoutManager implements Runnable {
   private Vector tolist = new Vector();
   Thread currThread = null;
   boolean asDefault = false;
   
   static private TimeoutManager globMgr = null;
   
   public TimeoutManager() {
   
   }
   
   public static TimeoutManager getGlobalManager() {
      if (globMgr == null) {
         globMgr = new TimeoutManager();
         globMgr.asDefault = true;
      }
      return globMgr;
   }
   
   public synchronized void removeTimeout(String id) {
      for(int i=0; i < tolist.size(); i++) {
         Timeout tto = (Timeout)tolist.elementAt(i);
         if (tto.getID().equals(id)) {
            tolist.removeElementAt(i);
            if (currThread != null) {
               currThread.interrupt();
            }
            
           // Continue processing, so we can remove all redundencies
            i--;
         }
      }
   }
   
   public synchronized void addTimeout(Timeout to) {
      if (currThread == null) {
         currThread = new Thread(this);
         currThread.setName("TimeoutManager");
         currThread.start();
      }
      
      if (to != null) {
         for(int i=0; i < tolist.size(); i++) {
            Timeout tto = (Timeout)tolist.elementAt(i);
            if (to.getTimeoutTime() <= tto.getTimeoutTime()) {
               tolist.insertElementAt(to, i);
               to = null;
               break;
            }
         }
         if (to != null) {
            tolist.addElement(to);
         }
      }
      
      currThread.interrupt();
   }
   
   public synchronized void shutdown() {
      DebugPrint.println(DebugPrint.INFO, "TM: Shutdown called!");
      System.out.flush();
      tolist.removeAllElements();
      if (currThread != null) {
         currThread = null;
         currThread.interrupt();
      }
   }
   
   public void run() {
      while(currThread == Thread.currentThread()) {
         if (asDefault && this != globMgr) {
            DebugPrint.println(DebugPrint.WARN, 
                               "TimeMgr: GlobMgr is != me ... bag out");
            return;
         }
         Vector popped = null;
         synchronized (this) {
            try {
               long curmillis = System.currentTimeMillis();
               Timeout to = null;
               if (tolist.size() > 0) {
                  to = (Timeout)tolist.firstElement();
               }
               if (to != null) {
                  long sleeptime = to.getTimeoutTime() - curmillis;
                  if (sleeptime > 0) {
                     wait(sleeptime);
                  }
               } else {
                  wait(60000*60*24);
               }
            } catch (InterruptedException e) {
            }
            
           // System.out.println("TOM: " + toString());
            
            long curmillis = System.currentTimeMillis();
            for(int i=0; i < tolist.size(); i++) {
               Timeout to = (Timeout)tolist.elementAt(i);
               if (to.getTimeoutTime() < curmillis) {
                  if (popped == null) popped = new Vector();
                  popped.addElement(to);
                  tolist.removeElementAt(i);
                  i--;
               }
            }
         }
         
         if (popped != null) {
            Enumeration e = popped.elements();
            while(e.hasMoreElements()) {
               Timeout to = (Timeout)e.nextElement();
               try {
                  to.process();
               } catch(Throwable tt) {
                  DebugPrint.printlnd(DebugPrint.ERROR, 
                                      "TimeoutManager: Exception during process");
                  DebugPrint.println(DebugPrint.ERROR, tt);
                  if (tt instanceof java.lang.ThreadDeath) {
                     throw (java.lang.ThreadDeath)tt;
                  }
               }
            }
         }
      }
   }
   
   public String toString() {
      return "TimeoutManager: " + tolist.size() + " timeouts:\n" + tolist.toString();
   }
}
