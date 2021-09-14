package oem.edge.ed.odc.meeting.client;

import  oem.edge.ed.odc.meeting.common.*;
import  oem.edge.ed.odc.dsmp.common.*;

import java.util.*;
import java.lang.*;
import java.io.*;

public class BScraper extends DSMPDispatchBase {

   public class MissingDependencies extends Exception {
      MissingDependencies(String s) {
         super(s);
      }
   }
   public class PossibleMissingDependencies extends Exception {
      PossibleMissingDependencies(String s) {
         super(s);
      }
   }
   

  /* ----------------------------------------------------------------------*\
  ** ----------------------------------------------------------------------**
  ** -------------  Protocol Definition for BScraper to EXE ---------------**
  ** ----------------------------------------------------------------------**
  \* ----------------------------------------------------------------------*/
  
  /*
  **   ======================================================================
  **   Connect                -  Connect to Display
  **      
  **      flags  : bit0 set if VIEWONLY bit1 set if FORCE
  **
  **      B1     : len
  **      B2-Bn  : Display name
  **
  **   ConnectReply           - 
  **
  **      Flags  : Bit0 set if success
  **      B1-B4  : Return code
  **      B5-B6  : string len
  **      B7-Bn  : possible explanation
  */
  /*
  **   ======================================================================
  **   Disconnect             -  disconnect from Display
  **      
  **      flags  : NA
  **
  */
  /*
  **   ======================================================================
  **   SetScrapeDelay         -  Set the scrape delay
  **      
  **      flags  : NA
  **
  **      B1-B4  : delay
  */
  /*
  **   ======================================================================
  **   Pause/Resume           -  disconnect from Display
  **      
  **      flags  : NA
  */
  /*
  **   ======================================================================
  **   GetDesktopWindow       -  Return the handle for the Desktop Window
  **      
  **      flags  : NA
  **
  **   GetDesktopWindowReply  - 
  **
  **      Flags  : bit 0 set if success, 0 otherwise.
  **
  **      B1-B4  : desktopID
  */
  /*
  **   ======================================================================
  **   IsModeStillValid       -  Queries if current scraping mode still valid
  **      
  **      flags  : NA
  **
  **   IsModeStillValidReply  - 
  **
  **      Flags  : bit 0 set if success, 0 otherwise.
  */
  /*
  **   ======================================================================
  **   GetTopLevelWindows     -  Queries for toplevel window list
  **      
  **      flags  : NA
  **
  **   GetTopLevelWindowsReply -  
  **
  **      Flags  : bit 0 set if success, 0 otherwise.
  **
  **      B1-B2  : number of windows
  **      Y1-Y4  : window handled, repeated for numwindows
  */
  /*
  **   ======================================================================
  **   ConfigureToDesktop      -  Configure scraper to Desktop scraping
  **      
  **      flags  : NA
  **
  **   ConfigureToDesktopReply -
  **
  **      Flags  : bit 0 set if success, 0 otherwise.
  */
  /*
  **   ======================================================================
  **   ConfigureToWindow       -  Configure scraper to window scraping
  **      
  **      flags  : NA
  **
  **      B1-B4  : Window id
  **
  **   ConfigureToWindowReply  -
  **
  **      Flags  : bit 0 set if success, 0 otherwise.
  */
  /*
  **   ======================================================================
  **   Configure               -  Configure to area
  **      
  **      flags  : NA
  **
  **      B1-B4  : x
  **      B5-B8  : y
  **      B9-B12 : w
  **      B13-B16: h
  **
  **   ConfigureReply          -
  **
  **      Flags  : bit 0 set if success, 0 otherwise.
  */
  /*
  **   ======================================================================
  **   GetWindowTitle          -  Returns the window title
  **      
  **      flags  : NA
  **
  **      B1-B4  : window id
  **
  **   GetWindowTitleReply     -
  **
  **      Flags  : bit 0 set if success, 0 otherwise.
  **
  **      B1-B2  : title len
  **      B3-Bn  : titlestring
  */
  /*
  **   ======================================================================
  **   GetCursorPosition       -  Returns the current cursor pos
  **      
  **      flags  : NA
  **
  **   GetCursorPositionReply  -
  **
  **      Flags  : bit 0 set if success, 0 otherwise.
  **
  **      B1-B4  : x
  **      B5-B8  : y
  */
  /*
  **   ======================================================================
  **   GetCurrentFrame         -  Returns the current frame dimensions
  **      
  **      flags  : NA
  **
  **   GetCurrentFrameReply    -
  **
  **      Flags  : bit 0 set if success, 0 otherwise.
  **
  **      B1-B4  : x
  **      B5-B8  : y
  **      B9-B12 : w
  **      B13-B16: h
  */
  /*
  **   ======================================================================
  **   GetNewFrame             -  Gets and returns new frame dimensions
  **      
  **      flags  : NA
  **
  **   GetNewFrameReply        -
  **
  **      Flags  : bit 0 set if success, 0 otherwise.
  **
  **      B1-B4  : x
  **      B5-B8  : y
  **      B9-B12 : w
  **      B13-B16: h
  */
  /*
  **   ======================================================================
  **   ReplayLastFrame         -  Replay last frame
  **      
  **      flags  : NA
  **
  **   ReplayLastFrameReply    -
  **
  **      Flags  : bit 0 set if success, 0 otherwise.
  **
  **      B1-B4  : x
  **      B5-B8  : y
  **      B9-B12 : w
  **      B13-B16: h
  */
  /*
  **   ======================================================================
  **   GetUpdatedPels          -  Get next block of data
  **      
  **      flags  : NA
  **
  **   GetUpdatedPelsReply     -
  **
  **      Flags  : bit 0 set if good data, 0 otherwise.
  **
  **      B1-B4  : x
  **      B5-B8  : y
  **      B9-B12 : w
  **      B13-B16: h
  **      B17-B20: sz
  **      B17-Bn : sz bytes
  **
  **    b1-bn may repeat multiple times ... hack to speed up linux
  */
  /*
  **   ======================================================================
  **   InjectKey               -  Inject key into server
  **      
  **      flags  : Bit0 set if Keypress (release otherwise)
  **               Bit1 set if keyCode  (Char otherwise)
  **
  **      B1-B4  : x
  **      B5-B8  : y
  **      B9-B12 : key
  */
  /*
  **   ======================================================================
  **   InjectMouse             -  Inject mouseevent into server
  **      
  **      flags  : Bit0 set if Buttonpress (release otherwise)
  **
  **      B1-B4  : x
  **      B5-B8  : y
  **      B9-B12 : butnum
  */
   private static final byte OP_CONNECT                          = 1;
   private static final byte OP_DISCONNECT                       = 2;
   private static final byte OP_SETSCRAPE_DELAY                  = 3;
   private static final byte OP_PAUSE                            = 4;
   private static final byte OP_RESUME                           = 5;
   private static final byte OP_GET_DESKTOP_WINDOW               = 6;
   private static final byte OP_IS_MODE_STILL_VALID              = 7;
   private static final byte OP_GET_TOPLEVEL_WINDOWS             = 8;
   private static final byte OP_CONFIGURE_TO_DESKTOP             = 9;
   private static final byte OP_CONFIGURE_TO_WINDOW              = 10;
   private static final byte OP_CONFIGURE                        = 11;
   private static final byte OP_GET_WINDOW_TITLE                 = 12;
   private static final byte OP_GET_CURSOR_POSITION              = 13;
   private static final byte OP_GET_CURRENT_FRAME                = 14;
   private static final byte OP_GET_NEW_FRAME                    = 15;
   private static final byte OP_REPLAY_LAST_FRAME                = 16;
   private static final byte OP_GET_UPDATED_PELS                 = 17;
   private static final byte OP_INJECT_KEY                       = 18;
   private static final byte OP_INJECT_MOUSE                     = 19;
   private static final byte OP_SELECT_WINDOW                    = 20;
   
   private static final byte OP_CONNECT_REPLY                    = 41;   
   private static final byte OP_GET_DESKTOP_WINDOW_REPLY         = 42;   
   private static final byte OP_IS_MODE_STILL_VALID_REPLY        = 43;   
   private static final byte OP_GET_TOPLEVEL_WINDOWS_REPLY       = 44;   
   private static final byte OP_CONFIGURE_TO_DESKTOP_REPLY       = 45;   
   private static final byte OP_CONFIGURE_TO_WINDOW_REPLY        = 46;   
   private static final byte OP_CONFIGURE_REPLY                  = 47;   
   private static final byte OP_GET_WINDOW_TITLE_REPLY           = 48;   
   private static final byte OP_GET_CURSOR_POSITION_REPLY        = 49;   
   private static final byte OP_GET_CURRENT_FRAME_REPLY          = 50;   
   private static final byte OP_GET_NEW_FRAME_REPLY              = 51;   
   private static final byte OP_REPLAY_LAST_FRAME_REPLY          = 52;   
   private static final byte OP_GET_UPDATED_PELS_REPLY           = 53;   
   private static final byte OP_SELECT_WINDOW_REPLY              = 54;

   private static final byte OP_CURSOR_UPDATE_EVENT              = 81;
   
   private int           jnidata[];
   private int           scrapeDelay = 250;
   private int           debugLev = 0;
   
   private boolean       newpos = true;
   private Byte          newposSync = new Byte((byte)1);
   
   private CompressInfo lastpels = null;
   
   String platinfo        = "";
   boolean loaded         = false;
   boolean tryloaded      = false;
   
  // Used for true BScraper stuff
   Hashtable waitingReply  = null;
   int cursorx = 0;
   int cursory = 0;
   DSMPBaseHandler handler = null;
   
  // 0 seq is used for asynch events from 'server'
   byte    seq         = 0;
   protected synchronized byte getSeq() {
      if (++seq == (byte)0) {
         seq++;
      }
      return seq;
   }
   
  // All 'Scrapers' should call super() to ensure this happens
   BScraper(String suffix) {
      platinfo = (suffix != null)?suffix:"";
      jnidata = new int[10];
      for(int i=0; i < jnidata.length; i++) {
         jnidata[i] = 0;
      }
   }
      
  // Factory
   public static BScraper createInstance() {
      
      String osarch = System.getProperty("os.arch").toUpperCase();
      String osname = System.getProperty("os.name").toUpperCase();
      String osver  = System.getProperty("os.version").toUpperCase();
      boolean jniOrExe = true;
      
      String platinfo = "";
      if ((osname.indexOf("SOLARIS") >= 0   || 
           osname.indexOf("SUNOS")   >= 0)  && 
           osarch.indexOf("SPARC")   >= 0)  {
         
         platinfo="-SunOS";
         jniOrExe = false;
         
      } else if (osname.indexOf("AIX")     >= 0) {
         platinfo="-AIX";
      } else if (osname.indexOf("HP-UX")   >= 0) {
         platinfo="-HP-UX";
         jniOrExe = false;
      } else if (osname.indexOf("LINUX")   >= 0 &&
                (osarch.indexOf("86")      >= 0 ||
                 osarch.indexOf("X86")     >= 0)) {
         platinfo="-Linux86";
         jniOrExe = false;
      } else if (osname.indexOf("WINDOWS") >= 0) {
         platinfo="";
      }
      
      BScraper ret = null;
      if (jniOrExe) {
         ret = new DScraper(platinfo);
      } else {
         ret = new BScraper(platinfo);
      }
      
      System.out.println("DSMP platinfo: Name[" + osname   + 
                         "] Arch[" + osarch +
                         "] Ver [" + osver + "]");
      return ret;
   }
         
         
   public boolean scrapingEnabled() {
      return loadScraper(true);
   }
   
   public void setDebugLevel(int lev) {
      System.out.println("SET DEBUG not sending to Scraper ... TODO!");
      debugLev = lev;
   }
   public int getDebugLevel() {
      return debugLev;
   }
   
   // loadScraper() - load the library/exe to handle 'native' calls
   protected synchronized boolean loadScraper(boolean loud) {
   
      if (!tryloaded && !loaded) {
         
         String osarch = System.getProperty("os.arch").toUpperCase();
         String osname = System.getProperty("os.name").toUpperCase();
         String osver  = System.getProperty("os.version").toUpperCase();
         String rpi = "XScraper" + platinfo;
         String tail = "";
         if ((osname.indexOf("SOLARIS") >= 0   || 
              osname.indexOf("SUNOS")   >= 0)  && 
              osarch.indexOf("SPARC")   >= 0)  {
            rpi = "lib" + rpi;
            tail = ".so";
         } else if (osname.indexOf("AIX")     >= 0) {
            rpi = "lib" + rpi;
            tail = ".so";
         } else if (osname.indexOf("HP-UX")   >= 0) {
            rpi = "lib" + rpi;
            tail = ".sl";
         } else if (osname.indexOf("LINUX")   >= 0 &&
                   (osarch.indexOf("86")      >= 0 ||
                    osarch.indexOf("X86")     >= 0)) {
            rpi = "lib" + rpi;
            tail = ".so";
         } else if (osname.indexOf("WINDOWS") >= 0) {
            tail = ".dll";
         }
         
         for (int tryboth=0; !loaded && tryboth < 2; tryboth++) {
            if (tryboth == 1) rpi += "-green";
            File f = new File(rpi+tail);
            System.out.println("Load attempt on [" + rpi + tail + "]");
            if (f.exists() && f.isFile()) {
               try {
                  Process proc = Runtime.getRuntime().exec(
                     f.getAbsolutePath());
                  
                 // This will cause NO exception if 'delayed' error loading
                  try {
                     Thread.sleep(1000);
                     proc.exitValue();
                     System.out.println("Error loading executable");
                     continue;
                  } catch(IllegalThreadStateException itse) {
                  } catch(Exception eee) {
                  }
                  
                  handler = new DSMPBaseHandler(this);
                  
//                  handler.verbose = true;

                  handler.setInputOutput(proc.getInputStream(), 
                                         proc.getOutputStream());
                  waitingReply = new Hashtable();
                  
                  final InputStream errstream = proc.getErrorStream();
                  new java.lang.Thread("BScraper - remotereader") {
                        public void run() {
                           InputStreamReader strm = new InputStreamReader(errstream);
                           char errbuf[] = new char[1024];
                           try {
                              while(true) {
                                 int r = strm.read(errbuf);
                                 if (r == -1) break;
                                 if (r > 0) {
                                    System.out.print(new String(errbuf, 0, r));
                                 }
                              }
                           } catch(IOException ioe) {}
                           try {
                              strm.close();
                              errstream.close();
                           } catch(Exception eee) {}
                        }
                     }.start();
                  
                  loaded = true;
                  tryloaded = false;
                  break;
//               setDebug(true);
               } catch(IOException io) {
                  System.out.println("IO Exception Exec file");
               }
            }
         }         
         
         if (!loaded) {
            tryloaded = true;
            System.out.println("Load attempt failed");
         }
      }
      return loaded;
   }
   
   public void scrapingDelay(int v) { 
      scrapeDelay = v;    
      scrapingDelayI(v);
   }
   public int  scrapingDelay()      { 
      return scrapeDelay; 
   }
   public void newPosAvailable() {
      synchronized(newposSync) {
         newpos=true;
         newposSync.notifyAll();
      }
   }
   
   public java.awt.Point           getCursorPostion_Wait() {
      synchronized(newposSync) {
         if (!newpos) {
            try {
               newposSync.wait(40000);
            } catch(InterruptedException tt) {}
         } else {
         }
         newpos = false;
      }
      return getCursorPosition();
   }

   static public int[] runlengthDecode(byte arr[], int ret[], int ofs, 
                                       int len, int unencodedLen) {
      
      if (ret == null || ret.length < unencodedLen) 
         ret = new int[unencodedLen];
      
//      System.out.println("---Decode--- len=" + len + " ulen=" +unencodedLen);
      int i=ofs;
      int o=0;
      len += ofs;
      while(i < len) {
         byte l = arr[i++];
         if (l < 0) {
            int realL = ((int)(-l)) + 1;
            int v = 0xff000000 | ((((int)arr[i++]) & 0xff) << 16);
            v |= ((((int)arr[i++]) & 0xff) << 8);
            v |= ((((int)arr[i++]) & 0xff));
//            System.out.println("OP = Same[" + l + "] [" + realL + "] Val[" + v + "]");
            while(realL-- > 0) {
               ret[o++] = v;
            }
         } else {
            int realL = ((int)(l)) + 1;
//            System.out.println("OP = DIFF[" + l + "] [" + realL + "]");
            while(realL-- > 0) {
               int v = 0xff000000 | ((((int)arr[i++]) & 0xff) << 16);
               v |= ((((int)arr[i++]) & 0xff) << 8);
               v |= ((((int)arr[i++]) & 0xff));
//               System.out.println("\t" + o + ": [" + v + "]");
               ret[o++] = v;
            }
         }
      }
      return ret;
   }
   
   public synchronized void notifySenders() {
      notifyAll();
   }
   
   private void send(DSMPBaseProto p) {
      if (loaded) {
         handler.sendProtocolPacket(p);
      }   
   }
   
   private DSMPBaseProto send(DSMPBaseProto p, byte replyOp) {
      DSMPBaseProto ret = null;
      byte enterOp = p.getOpcode();
      if (loaded) {
         Byte b = new Byte(p.getHandle());
         DSMPBaseProto tp = (DSMPBaseProto)waitingReply.get(b);
         if (tp != null) {
            System.out.println("Zoinks! waiting reply already HAS proto!");
            System.out.println("Me = " + p.toString() + 
                               "\nHim = " + tp.toString());
         }
         synchronized (this) {
            waitingReply.put(b, p);
//            System.out.println("Sending Packet");
            handler.sendProtocolPacket(p);
            
            while(loaded) {
               tp = (DSMPBaseProto)waitingReply.get(b);
               if (tp == null) {
                  System.out.println("Zoinks! tp is NULL!");
                  break;
               } else if (tp.getOpcode() != enterOp) {
                  if (tp.getOpcode() != replyOp) {
                     System.out.println(
                        "Zoinks! reply op is != than what we wanted!");
                     System.out.println("IN op = "        + enterOp + 
                                        " Expected op = " + replyOp +
                                        " Got\n\t" + tp.toString());
                     break;
                  }
//                  System.out.println("Returning with reply");
                  ret = tp;
                  waitingReply.remove(b);
                  break;
               } else {
                  try { 
//                     System.out.println("Waiting for reply");
                     wait();
                  } catch(InterruptedException ee) {}
               }
            }
         }
      }         
      return ret;
   }
   
   public void doshutdown(Throwable tt) {
      System.out.println("Shutdown on proto/comm error");
      if (tt != null) {
         tt.printStackTrace(System.out);
      }
      
      if (handler != null) {
         handler.shutdown();
      } else {
         System.out.println("Handler is null for shutdown!");
      }
   }
   
  // Match defines in XScraper.h
   private final static int FAILURE_TO_CONNECT_TO_DISPLAY  = 1;
   private final static int CANT_CHECK_IF_APAR_INSTALLED   = 33;
   private final static int INCONCLUSIVE_APAR_CHECK        = 44;
   private final static int APAR_NOT_INSTALLED             = 66;
   private final static int APAR_IS_INSTALLED              = 100;
   private final static int CONNECT_SUCCESSFUL             = 0;
   
   public  int    connect(String host, boolean viewonly, boolean force) 
                   throws MissingDependencies, PossibleMissingDependencies {
      
      if (!loaded && !loadScraper(true)) {
        ((String)null).length();  // cause NullPointer Exception
      }
      
      DSMPBaseProto p = new DSMPBaseProto(OP_CONNECT, 
                                          (byte)((viewonly?1:0)|(force?2:0)), getSeq());
      p.appendString8(host);
      p = send(p, OP_CONNECT_REPLY);
      
      
      int ret = 1;
      String ans = null;
      try {
         ret = p.getInteger();
         ans = p.getString16();
      } catch(InvalidProtocolException ipe) {}
      
      Exception e = null;
      
      if (ret != 0) handler.shutdown();
         
      switch(ret) {
         case FAILURE_TO_CONNECT_TO_DISPLAY:
            break;
         case CONNECT_SUCCESSFUL:
            break;
         case APAR_NOT_INSTALLED:
            throw new MissingDependencies(ans);
         case CANT_CHECK_IF_APAR_INSTALLED:
         case INCONCLUSIVE_APAR_CHECK: 
         default:
            throw new PossibleMissingDependencies(ans);
      }
      return ret;
   }
   
   public  void    disconnect() {
      if (!loaded) ((String)null).length();  // cause NullPointer Exception
      
      DSMPBaseProto p = new DSMPBaseProto(OP_DISCONNECT, (byte)0, getSeq());
      send(p);
   }
   
   public  int     selectWindow() {
      if (!loaded) ((String)null).length();  // cause NullPointer Exception
      
      DSMPBaseProto p = new DSMPBaseProto(OP_SELECT_WINDOW, (byte)0, getSeq());
      p = send(p, OP_SELECT_WINDOW_REPLY);
      
      int ret = -1;
      try {
         ret = p.getInteger();
      } catch(Exception ee) { doshutdown(ee); }
      return ret;
   }
   
   protected void    scrapingDelayI(int v) {
      if (!loaded) ((String)null).length();  // cause NullPointer Exception
      
      DSMPBaseProto p = new DSMPBaseProto(OP_SETSCRAPE_DELAY, 
                                          (byte)0, getSeq());
      p.appendInteger(v);
      send(p);
   }
   
   public  void    pause() {
      if (!loaded) ((String)null).length();  // cause NullPointer Exception
      
      lastpels = null;
      
      DSMPBaseProto p = new DSMPBaseProto(OP_PAUSE, (byte)0, getSeq());
      send(p);
   }
   public  void    resume() {
      if (!loaded) ((String)null).length();  // cause NullPointer Exception
      DSMPBaseProto p = new DSMPBaseProto(OP_RESUME, (byte)0, getSeq());
      send(p);
   }
   
   public  int     getDesktopWindow() {
      if (!loaded) ((String)null).length();  // cause NullPointer Exception
      DSMPBaseProto p = new DSMPBaseProto(OP_GET_DESKTOP_WINDOW, 
                                          (byte)0, getSeq());
      p = send(p, OP_GET_DESKTOP_WINDOW_REPLY);
      
      int ret = -1;
      try {
         ret = p.getInteger();
      } catch(Exception ee) { doshutdown(ee); }
      return ret;
   }
   
   public  boolean isModeStillValid() {
      if (!loaded) ((String)null).length();  // cause NullPointer Exception
      
      DSMPBaseProto p = new DSMPBaseProto(OP_IS_MODE_STILL_VALID, 
                                          (byte)0, getSeq());
      p = send(p, OP_IS_MODE_STILL_VALID_REPLY);
      return p.bitsSetInFlags(1);
   }
   
   public  Vector  getToplevelWindows() {
      Vector ret = new Vector();
      if (!loaded) ((String)null).length();  // cause NullPointer Exception
      
      DSMPBaseProto p = new DSMPBaseProto(OP_GET_TOPLEVEL_WINDOWS, 
                                          (byte)0, getSeq());
      p = send(p, OP_GET_TOPLEVEL_WINDOWS_REPLY);
      try {
         int num = p.getShort();
         while(num-- > 0) {
            ret.addElement(new Integer(p.getInteger()));
         }
      } catch(Exception ee) { doshutdown(ee); }
      return ret;
   }
   
   public  int     configureToDesktop() {
      if (!loaded) ((String)null).length();  // cause NullPointer Exception
      
      lastpels = null;
      
      DSMPBaseProto p = new DSMPBaseProto(OP_CONFIGURE_TO_DESKTOP, 
                                          (byte)0, getSeq());
      p = send(p, OP_CONFIGURE_TO_DESKTOP_REPLY);
      
      int ret = -1;
      try {
         ret = (p.getFlags() & (byte)1);
      } catch(Exception ee) { doshutdown(ee); }
      return ret;
   }
   
   public  int     configureToWindow(int window) {
      if (!loaded) ((String)null).length();  // cause NullPointer Exception
      
      lastpels = null;
      
      DSMPBaseProto p = new DSMPBaseProto(OP_CONFIGURE_TO_WINDOW, 
                                          (byte)0, getSeq());
      p.appendInteger(window);
      p = send(p, OP_CONFIGURE_TO_WINDOW_REPLY);
      
      int ret = -1;
      try {
         ret = (p.getFlags() & (byte)1);
      } catch(Exception ee) { doshutdown(ee); }
      return ret;
   }
   
   public  int     configure(int x, int y, int w, int h) {
      if (!loaded) ((String)null).length();  // cause NullPointer Exception
      
      lastpels = null;
      
      DSMPBaseProto p = new DSMPBaseProto(OP_CONFIGURE, 
                                          (byte)0, getSeq());
      p.appendInteger(x);
      p.appendInteger(y);
      p.appendInteger(w);
      p.appendInteger(h);
      p = send(p, OP_CONFIGURE_REPLY);
      
      int ret = -1;
      try {
         ret = (p.getFlags() & (byte)1);
      } catch(Exception ee) { doshutdown(ee); }
      return ret;
   }
   
   public  String getWindowTitle(int window) {
      if (!loaded) ((String)null).length();  // cause NullPointer Exception
      DSMPBaseProto p = new DSMPBaseProto(OP_GET_WINDOW_TITLE, 
                                          (byte)0, getSeq());
      p.appendInteger(window);
      p = send(p, OP_GET_WINDOW_TITLE_REPLY);
      
      String ret = null;
      try {
         ret = p.getString16();
      } catch(Exception ee) { doshutdown(ee); }
      return ret;
   }
   
  // We have this info already
   public  java.awt.Point     getCursorPosition() {
      return new java.awt.Point(cursorx, cursory);
   }
   
   public  java.awt.Rectangle getCurrentFrame() {
      if (!loaded) ((String)null).length();  // cause NullPointer Exception
      
      DSMPBaseProto p = new DSMPBaseProto(OP_GET_CURRENT_FRAME, 
                                          (byte)0, getSeq());
      p = send(p, OP_GET_CURRENT_FRAME_REPLY);
      
      java.awt.Rectangle ret = null;
      try {
         int x, y, w, h;
         x=p.getInteger(); y=p.getInteger();
         w=p.getInteger(); h=p.getInteger();
         ret = new java.awt.Rectangle(x,y,w,h);
      } catch(Exception ee) { doshutdown(ee); }
      return ret;
   }
   
   public  java.awt.Rectangle getNewFrame(boolean forceupdate) {
      if (!loaded) ((String)null).length();  // cause NullPointer Exception
      
      lastpels = null;
      
      DSMPBaseProto p = new DSMPBaseProto(OP_GET_NEW_FRAME, 
                                  (byte)(forceupdate?1:0), getSeq());
      p = send(p, OP_GET_NEW_FRAME_REPLY);
      
      java.awt.Rectangle ret = null;
      try {
         int x, y, w, h;
         x=p.getInteger(); y=p.getInteger();
         w=p.getInteger(); h=p.getInteger();
         ret = new java.awt.Rectangle(x,y,w,h);
      } catch(Exception ee) { doshutdown(ee); }
      return ret;
   }
   
   public  java.awt.Rectangle replayLastFrame() {
      if (!loaded) ((String)null).length();  // cause NullPointer Exception
      
      lastpels = null;
      
      DSMPBaseProto p = new DSMPBaseProto(OP_REPLAY_LAST_FRAME, 
                                          (byte)0, getSeq());
      p = send(p, OP_REPLAY_LAST_FRAME_REPLY);
      
      java.awt.Rectangle ret = null;
      try {
         int x, y, w, h;
         x=p.getInteger(); y=p.getInteger();
         w=p.getInteger(); h=p.getInteger();
         ret = new java.awt.Rectangle(x,y,w,h);
      } catch(Exception ee) { doshutdown(ee); }
      return ret;
   }
   
   public  byte[]             getUpdatedPixelsInBytes2(byte a[]) {
      if (!loaded) ((String)null).length();  // cause NullPointer Exception
      
      
      byte ret[] = null;
               
      if (lastpels == null) {
         DSMPBaseProto p = new DSMPBaseProto(OP_GET_UPDATED_PELS, 
                                             (byte)0, getSeq());
         p = send(p, OP_GET_UPDATED_PELS_REPLY);
         
         try {
            if (p.bitsSetInFlags(1)) {
               lastpels = p.getDataAtCursor();
               
            } 
            
         } catch(Exception ee) { lastpels = null; doshutdown(ee); }
      }
      
      if (lastpels != null) {
         if (a == null) {
            a = new byte[(64 * 64 * 4) + 20];
         }
         ret = a;
         int ofs = lastpels.ofs+16;
         
         int len = (((int)lastpels.buf[ofs+0]) & 0xff) << 24 |
                   (((int)lastpels.buf[ofs+1]) & 0xff) << 16 |
                   (((int)lastpels.buf[ofs+2]) & 0xff) << 8  |
                   (((int)lastpels.buf[ofs+3]) & 0xff);
         len += 20;
         
/*         
         ofs=0;
         int v = (((int)lastpels.buf[ofs+0]) & 0xff) << 24 |
                 (((int)lastpels.buf[ofs+1]) & 0xff) << 16 |
                 (((int)lastpels.buf[ofs+2]) & 0xff) << 8  |
                 (((int)lastpels.buf[ofs+3]) & 0xff);
         System.out.print("x=" + v);
         
         ofs+=4;
         v = (((int)lastpels.buf[ofs+0]) & 0xff) << 24 |
                 (((int)lastpels.buf[ofs+1]) & 0xff) << 16 |
                 (((int)lastpels.buf[ofs+2]) & 0xff) << 8  |
                 (((int)lastpels.buf[ofs+3]) & 0xff);
         System.out.print(" y=" + v);
         
         ofs+=4;
         v = (((int)lastpels.buf[ofs+0]) & 0xff) << 24 |
                 (((int)lastpels.buf[ofs+1]) & 0xff) << 16 |
                 (((int)lastpels.buf[ofs+2]) & 0xff) << 8  |
                 (((int)lastpels.buf[ofs+3]) & 0xff);
         System.out.print(" w=" + v);
         
         ofs+=4;
         v = (((int)lastpels.buf[ofs+0]) & 0xff) << 24 |
                 (((int)lastpels.buf[ofs+1]) & 0xff) << 16 |
                 (((int)lastpels.buf[ofs+2]) & 0xff) << 8  |
                 (((int)lastpels.buf[ofs+3]) & 0xff);
         System.out.print(" h=" + v);
         
         System.out.println(" blen=" + lastpels.buf.length + " bofs=" + 
                            lastpels.ofs + " len=" + len+20);
*/       

         System.arraycopy(lastpels.buf, lastpels.ofs, a, 0, len);
         
         lastpels.ofs += len;
         lastpels.len -= len;
         if (lastpels.len <= 0) {
            lastpels = null;
         }
      }
      
      return ret;
   }
   
   public  void               injectKey(java.awt.Point point, 
                                        boolean keyPress, 
                                        boolean keyCodeOrChar,
                                        int     keysym) {
      if (!loaded) ((String)null).length();  // cause NullPointer Exception
      
      byte mask = (byte)(((keyPress?1:0)) | ((keyCodeOrChar?2:0)));
      DSMPBaseProto p = new DSMPBaseProto(OP_INJECT_KEY, mask, getSeq());
      p.appendInteger(point.x);
      p.appendInteger(point.y);
      p.appendInteger(keysym);
      send(p);
   }
   
   public  void               injectMouse(java.awt.Point point, 
                                          boolean buttonPress, 
                                          int     buttonNumber) {
      if (!loaded) ((String)null).length();  // cause NullPointer Exception
      
      byte mask = (byte)(((buttonPress?1:0)));
      DSMPBaseProto p = new DSMPBaseProto(OP_INJECT_MOUSE, mask, getSeq());
      p.appendInteger(point.x);
      p.appendInteger(point.y);
      p.appendInteger(buttonNumber);
      send(p);
   }
   
   
  /*
  ** How BScraper communicates with EXE
  */
   public void fireShutdownEvent(DSMPBaseHandler h) {
      loaded = false;
      notifySenders();
      synchronized(this) {
         handler = null;
         waitingReply = null;
      }
   }
   
   public void uncaughtProtocol(DSMPBaseHandler h, byte opcode) {
      System.out.println("Uncaught Proto - Shutdown : OP = " + opcode);
      handler.shutdown();
   }
   
   public void dispatchProtocolI(DSMPBaseProto proto, 
                                 DSMPBaseHandler handler, 
                                 boolean doDispatch) 
      throws InvalidProtocolException {
      
      if (!doDispatch) return;
      
      byte opcode = proto.getOpcode();
      if (opcode == OP_CURSOR_UPDATE_EVENT) {
         cursorx = proto.getInteger();
         cursory = proto.getInteger();
         newPosAvailable();
         return;
      }
      
      Byte handle = new Byte(proto.getHandle());
      switch(opcode) {
         
         case OP_CONNECT_REPLY: 
         case OP_GET_DESKTOP_WINDOW_REPLY: 
         case OP_IS_MODE_STILL_VALID_REPLY: 
         case OP_GET_TOPLEVEL_WINDOWS_REPLY: 
         case OP_CONFIGURE_TO_DESKTOP_REPLY: 
         case OP_CONFIGURE_TO_WINDOW_REPLY: 
         case OP_CONFIGURE_REPLY: 
         case OP_GET_WINDOW_TITLE_REPLY: 
         case OP_GET_CURSOR_POSITION_REPLY: 
         case OP_GET_CURRENT_FRAME_REPLY: 
         case OP_GET_NEW_FRAME_REPLY: 
         case OP_REPLAY_LAST_FRAME_REPLY: 
         case OP_GET_UPDATED_PELS_REPLY:
         case OP_SELECT_WINDOW_REPLY:
            
            DSMPBaseProto sentp = (DSMPBaseProto)waitingReply.get(handle);
            if (sentp != null) {
//               System.out.println("Received::\n" + proto);
               waitingReply.put(handle, proto);
               notifySenders();
            } else {
               System.out.println("Ugg. Got proto op = " + 
                                  opcode + " with handle " + handle + 
                                  " and NOOne waiting for it!!");
            }
            break;
         default: {
            System.out.println("Bad Proto=>\n" + proto.toString());   
            uncaughtProtocol(handler, opcode);
            break;
         }
      }
   }
   
   
   protected void finalize() {
      if (handler != null) {
         handler.shutdown();
      }
   }
}
