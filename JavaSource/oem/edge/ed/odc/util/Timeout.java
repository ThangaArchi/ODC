package oem.edge.ed.odc.util;

import java.util.*;
import java.lang.*;
 
public class Timeout implements Runnable, TimeoutListener {
   private long toTime;
   private TimeoutListener listener;
   private boolean ownthread = false;
   private String id;
   
   public Timeout(String idIn) {
      toTime = System.currentTimeMillis();
      listener = null;
      id = idIn;
   }
   
   public Timeout(Date to, String idIn, TimeoutListener listenerIn) {
      toTime = to.getTime();
      listener = listenerIn;
      id = idIn;
   }
   
   public Timeout(long msDelta, String idIn, TimeoutListener listenerIn) {
      toTime = System.currentTimeMillis() + msDelta;
      listener = listenerIn;
      id = idIn;
   }
   
   public String  getID()                   { return id; }
   public int     compareTo(Timeout oo) {
      return id.compareTo(oo.getID());
   }
   
   public void    setOwnThread(boolean v)   { ownthread = v;    }
   public boolean getOwnThread()            { return ownthread; }
    
   public long getTimeoutTime()             { return toTime;    }
   public void setTimeoutTime(long to)      { toTime = to   ;   }
   
   public void setTimeoutTimeDelta(long delta) { 
      toTime = System.currentTimeMillis() + delta; 
   }
   
   public TimeoutListener getListener()                    { return listener; }
   public void            setListener(TimeoutListener lin) { listener = lin;  }
   
   public void process() {
      if (listener == null) {
         listener = this;
      }
      if (ownthread) {
         Thread t = new Thread(this);
         t.setName("TimeoutInstance");
         t.start();
      } else {
         listener.tl_process(this);
      }     
   }
   
   public void run() {
      listener.tl_process(this);
   }
   
   public void tl_process(Timeout to) {
      System.out.println("TIMEOUT process in TO.java!");
      System.out.flush();
   }
   
   public String toString() {
      return "TimeoutId: " + id + " Secs till Pop: " + 
              ((toTime-System.currentTimeMillis())/1000) + "\n";
   }
}

 
