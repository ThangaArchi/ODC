package oem.edge.ed.odc.dsmp.common;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import oem.edge.ed.odc.tunnel.common.DebugPrint;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2002-2004                                     */
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

public class DSMPBaseHandler implements Runnable {

   protected long   bytesWritten  = 0;
   protected long   bytesRead     = 0;
   
   public long getTotalBytes()        { return bytesWritten + bytesRead; }
   public long getTotalBytesWritten() { return bytesWritten;             }
   public long getTotalBytesRead()    { return bytesRead;                }

   protected String identifier = "na";
   
   public String getIdentifier()         { return identifier; }
   public void   setIdentifier(String s) { identifier = s;    }
   
   protected int hdebuglev = 3;
   public int  handlerDebugLev()        { return hdebuglev; }
   public void handlerDebugLev(int lev) { hdebuglev = lev; }

   public boolean verbose = false;
   
  /*
  ** OutputRunner - Entire job is to allow folks to write into it 
  **                (ByteArrayOutputStream), and then to write to the real 
  **                stream which it harbors. Was a Filter before, but
  **                needed to change (forget why). Probably should be a 
  **                filter again.
  **
  **                When integrated into the Tunnel, this goes away (replaced 
  **                by SIB). Entire reason we have this is performance 
  **                increase.
  */
   class OutputRunner extends ByteArrayOutputStream implements Runnable {
      final int       MAX_BUF_SIZE = 150000;
      final int       ARRSZ        = 65536;
      DSMPBaseHandler handler      = null;
      OutputStream    out          = null;
      
      public OutputRunner(OutputStream os, DSMPBaseHandler hand) {
         out = os;
         handler = hand;
         new Thread(this, "DSMPBHand:OutputRunner").start();
      }
      
      public void close() throws IOException {
//         System.out.println("--OR: close()--");
//         (new Exception()).printStackTrace();
//         System.out.println("---------------");
         super.close();
         out.close();
         synchronized (this) {
            notifyAll();
         }
      }
      
      public synchronized void write(byte b[], int ofs, int len) {
         while (!handler.eof() && len > 0) {
            int sz = MAX_BUF_SIZE - size();
            if (sz >= len) {
               super.write(b, ofs, len);
               notifyAll();
               break;
            } else {
               if (sz > 0) {
                  super.write(b, ofs, sz);
                  ofs += sz;
                  len -= sz;
                  notifyAll();
               }
               if (!handler.eof()) {
                  try {
                     this.wait();
                  } catch(InterruptedException ie) {}
               }
            }
         } 
      }
      
      public void run() {
         byte arr[] = new byte[ARRSZ];
         try {
            while(!handler.eof()) {
               int sz;
               synchronized(this) {
                  sz = count < ARRSZ ? count : ARRSZ;
                  
                  if (sz > 0) {
                     System.arraycopy(buf, 0, arr,  0, sz);
                     count -= sz;
                     if (count > 0) {
                        System.arraycopy(buf, sz, buf, 0, count);
                     }
                     notifyAll();
                  }
               }
               
              // Take flush out of synchronize
               if (sz == 0) { 
                  out.flush();
                  synchronized(this) {
                     if (count == 0) {
                        try {
                           wait();
                        } catch(Throwable tt) {}
                     }
                  }                  
               }
               if (sz > 0) {
                  out.write(arr, 0, sz);
               }
            }
         } catch(Throwable ttt) {
//            System.out.println("OR: Exception! --->");
//            ttt.printStackTrace();
            handler.shutdown();
         }
         
        // Wake up anyone waiting on us
         synchronized(this) {
            reset();
            notifyAll();
         }
         if (handler.verbose) System.out.println("OutputRunner Done");
      }
   }

  /*
  ** MyDeflaterOutputStreamNC - Dbug only. Replaces the real Compression filter
  */
   class MyDeflaterOutputStreamNC extends FilterOutputStream {
      MyDeflaterOutputStreamNC(OutputStream os) {
         super(os);
      }
   }
  /*
  ** MyDeflaterInputStreamNC - Debug only. Replaces the real Compression filter
  */
   class MyInflaterInputStreamNC extends FilterInputStream {
      MyInflaterInputStreamNC(InputStream is) {
         super(is);
      }
   }
   
  /*
  ** MyDeflaterOutputStream - Filter to compress data being written to OS. 
  **                          Don't reset the Deflater until a flush is 
  **                          received. AND, allows restarting compression 
  **                          again (DeflaterOutputStream et al. is DONE once 
  **                          its been 'finished')
  */
   class MyDeflaterOutputStream extends FilterOutputStream {
   
      Deflater deflater = new Deflater(Deflater.BEST_SPEED);
      byte buf[] = new byte[16*1024];
      long lasttime = 0;
      
      MyDeflaterOutputStream(OutputStream os) {
         super(os);
         try {
            deflater.setStrategy(Deflater.DEFAULT_STRATEGY);
           // Setting any strategy other then default seems to break things ;-(
//            deflater.setStrategy(Deflater.HUFFMAN_ONLY);
         } catch(IllegalArgumentException ex) {
         }
      }
/*      
      public synchronized void write(byte b[], int ofs, int len) throws IOException {
         
         out.write(b, ofs, len);
         long ct = System.currentTimeMillis();
         System.out.println("--- Non-Zero Write [" + len + "] totin[" + 
                            deflater.getTotalIn() + "] totout[" + 
                            deflater.getTotalOut() + "]" + " : " + (ct-lasttime));
         lasttime = ct;
      }
      
      public synchronized void flush() throws IOException {
         out.flush();
         long ct = System.currentTimeMillis();
         System.out.println("--- FLUSH Write [" + 0 + "] totin[" + 
                            deflater.getTotalIn() + "] totout[" + 
                            deflater.getTotalOut() + "]" + " : " + (ct-lasttime));
         lasttime = ct;
         out.flush();
      }
*/      
      public synchronized void write(byte b[], int ofs, int len) throws IOException {
         
//         System.out.println("--- Write ofs=" + ofs + " len = " + len);
         deflater.setInput(b, ofs, len);
         int numcomp = 0;
         int tot = 0;
         while((numcomp = deflater.deflate(buf, 0, buf.length)) > 0) {
//            System.out.println("   Deflate numcomp=" + numcomp);
            tot += numcomp;
            out.write(buf, 0, numcomp);
         }
//         System.out.println("--- Write " + len + " -> " + tot);
/*
         if (tot != 0) {
            long ct = System.currentTimeMillis();
            System.out.println("--- Non-Zero Write [" + tot + "] totin[" + 
                               deflater.getTotalIn() + "] totout[" + 
                               deflater.getTotalOut() + "]" + " : " + (ct-lasttime));
            lasttime = ct;
         }
*/
      }
      
      public synchronized void flush() throws IOException {
//         System.out.println("--- Calling FLUSH");
         deflater.finish();
         int numcomp;
         int tot = 0;
         while((numcomp = deflater.deflate(buf, 0, buf.length)) > 0) {
//            System.out.println("   FlushDeflate numcomp=" + numcomp);
            out.write(buf, 0, numcomp);
            tot += numcomp;
         }
        /*
         if (tot != 0) {
            long ct = System.currentTimeMillis();
            System.out.println("--- FLUSH Write [" + tot + "] totin[" + 
                               deflater.getTotalIn() + "] totout[" + 
                               deflater.getTotalOut() + "]" + " : " + (ct-lasttime));
            lasttime = ct;
         }
        */
         deflater.reset();
         out.flush();
      }
   }
   
  /*
  ** MyInflaterInputStream - See MyDeflaterOutputStream
  */
   class MyInflaterInputStream extends FilterInputStream {
   
      Inflater inflater = new Inflater();
      int     lastlen   = 0;
      int     lastofs   = 0;
      byte buf[] = new byte[16*1024];
      
      MyInflaterInputStream(InputStream is) {
         super(is);
      }
      
      public synchronized void mark(int readlimit) {}
      public boolean markSupported()               {return false;}
      public synchronized long skip(long n) throws IOException {
         byte lbuf[] = new byte[4096];
         long tn = n;
         while(n > 0) {
            int r = read(lbuf, 0, (int)java.lang.Math.min(n, lbuf.length));
            if (r == -1) break;
            n -= r;
         }
         return tn - n;
      }
      
      public synchronized int read(byte b[], int ofs, int len) throws IOException {
//         System.out.println("--- Read ofs=" + ofs + " len = " + len);
         int totdone = 0;
         while(totdone == 0) {
            if (inflater.finished()) {
               int remain = inflater.getRemaining();
               inflater.reset();
//               System.out.println("MyInflater: Reset with " + remain + " remaining");
               if (remain > 0) {
                  lastofs+=(lastlen-remain);
                  inflater.setInput(buf, lastofs, lastlen=remain);
               }
            }
            
            if (inflater.needsInput()) {
               if ((lastlen=in.read(buf, lastofs=0, buf.length)) < 0) {
//                  System.out.println("MyInflater: EOF " + 
//                                     inflater.getRemaining() + " remaining");
                  return -1;
               }
               inflater.setInput(buf, 0, lastlen);
            }
            int numcomp = 0;
            try {
               while(len > 0 && (numcomp = inflater.inflate(b, ofs+totdone, len)) > 0) {
//                  System.out.println("   Inflate numcomp=" + numcomp);
                  len-=numcomp;
                  totdone += numcomp;
               }
            } catch(DataFormatException de) {
               System.out.println("DFE: close()");
               in.close();
               return -1;
            }
         }
         return totdone;
      }
   }

  /*
  ** Reader - Thread charged with reading incoming protocol and dispatching it 
  **          to the Dispatcher.
  **
  **          This COULD be done outside of these walls, but it is here for 
  **          convenience.
  */
   class Reader extends Thread {
      DSMPBaseHandler handler = null;
      public Reader(DSMPBaseHandler hand) {
         handler = hand;
      }
      public void run() {
         try {
            while(!handler.eof()) {
               try {
                  DSMPBaseProto proto = handler.readProtocolPacket();
                  bytesRead += proto.size();
                  try {
                     handler.getDispatch().dispatchProtocol(proto, handler);
                  } catch(InvalidProtocolException ex) {
                     ex.printStackTrace(System.out);
                     System.out.println(proto.toString());
                     handler.shutdown();
                     break;
                  }
               } catch(IOException io) {
                  String msg = io.getMessage();
                  if (msg != null && msg.indexOf("EOF") >= 0) {
                     ;
                  } else if (!handler.eof()){
                     io.printStackTrace(System.out);
                  }
                  handler.shutdown();
                  break;
               } catch(Throwable tt) {
                  if (!handler.eof()) {
                     tt.printStackTrace(System.out);
                     handler.shutdown();
                  }
                  break;
               }
            }
         } catch(Throwable ttt) { }
         if (handler.verbose) System.out.println("DSMPReader Done");
      }
   }
   
   protected InputStream      istream         = null;
   protected OutputStream     ostream         = null;
   
   protected Vector           tosend          = new Vector();
   protected Vector           addInfo         = new Vector();
   protected int              curAddInfo      = 0;
   protected Byte             addLock         = new Byte((byte)0);
   
   protected boolean   toSendHasLowPriority   = false; 
   protected boolean addInfoHasNormalPriority = false; 
   
   protected boolean          done            = false;

   protected Thread           writethread     = null;
   protected Reader           reader          = null;
   
   protected int              handlerid       = IDGenerator.getId();
   
   protected DSMPDispatchBase dispatch        = null;
   protected int              flags           = 0;
   
   protected long             lastSendTimeA   = 0;
   protected long             lastSendTimeB   = 0;
   
   protected ProtocolFactory protoFactory     = null;
      
   protected static final int NUMSENT_INIT    = 100;
   
  // Max number of bytes that can be on tosend and addInfo combined
  //  If more than this, block the sender.
   protected int maxProtoByteCount = 6*1024*1024;
   public int  getMaxProtoByteCount()        { return maxProtoByteCount; }
   public void setMaxProtoByteCount(int nmh) { maxProtoByteCount = nmh;  }
   
   protected int queuedProtoByteCount = 0;
   
   
   protected long currSendInfo   = NUMSENT_INIT * 100000 * 1000;
   protected long nextSendInfo   = 0;
   protected int  numsent        = NUMSENT_INIT;
   
   protected boolean doRunner       = true;
   protected boolean doCompression  = false;
   
   public void    setDoRunner(boolean v)      { doRunner = v;         }
   public boolean getDoRunner()               { return doRunner;      }
   public void    setDoCompression(boolean v) { doCompression = v;    }
   public boolean getDoCompression()          { return doCompression; }
   
   public DSMPBaseHandler(DSMPDispatchBase disp) {
      super();
      dispatch = disp;
   }
   
   
   public void             addMaskToFlags(int v)      { flags |= v;          }
   public void             removeMaskFromFlags(int v) { flags &= ~v;         }
   public boolean          bitsSetInFlags(int v)      { return (flags&v)==v; }
   public int              getFlags()                 { return flags;        }
   public DSMPDispatchBase getDispatch()              { return dispatch;     }
   public int              getHandlerId()             { return handlerid;    }
   
   public void setProtocolFactory(ProtocolFactory p) {
      protoFactory = p;
   }
   
   public void startWriting() {
//      System.out.println("StartWriting Called");
      if (writethread == null && !done) {
         synchronized (addLock) {
            if (writethread == null && !done && ostream != null) {
               writethread = new Thread(this, "DSMPBHand:writethrd");
              /* This may have been screwing me up ... No GC
               try {
                  writethread.setPriority(Thread.MAX_PRIORITY);
               } catch(Throwable tt) {
                  tt.printStackTrace(System.out);
               }
              */
               writethread.start();
               reader = new Reader(this);
               reader.setName("DSMPBHand:Reader");
               reader.start();
            }
         }
      }
   }
   
  /*
  ** Set a new source/dest for protocol
  */
   public synchronized void setInputOutput(InputStream is, OutputStream os) {
      closeStreams();
      
      if (doRunner) {
         os = new OutputRunner(os, this);
      }
      
      if (doCompression) {
         istream = new MyInflaterInputStream(new BufferedInputStream(is));
         ostream = new MyDeflaterOutputStream(os);
      } else {
         istream = new BufferedInputStream(is);
         ostream = os;
      }
      
      done=false;
      startWriting();
   }
   
   public boolean eof() { return done; }
   
   public void shutdown() {
      synchronized(addLock) {
         if (!done) {
            done = true;
//            System.out.println("-Shutting down- " + System.currentTimeMillis());
//            (new Exception()).printStackTrace();
//            System.out.println("---------------");
            
            addLock.notifyAll();
         }         
      }      
   }
   
  /*
  ** Clean up our resources
  */
   public void finalize() {
      if (!done) {
         try {
            closeStreams();
         } catch(Exception ee) {}
      }
   }
   
  /*
  ** Clean up our resources
  */
   protected void closeStreams() {
   
//      System.out.println("----CSCLOSE----");
//      (new Exception()).printStackTrace();
//      System.out.println("---------------");
      
      if (istream != null) {
         try {
            istream.close();
         } catch(Throwable tt) {}
         istream = null;
      }
      if (ostream != null) {
         try {
            ostream.close();
         } catch(Throwable tt) {}
         ostream = null;
      }
   }
   
   protected DSMPBaseProto instanceProtocol() {
      DSMPBaseProto ret = null;
      if (protoFactory != null) {
         ret = protoFactory.createInstance();
      } else {
         ret = new DSMPBaseProto();
      }
      return ret;
   }

  /*
  ** Get next packet of protocol
  */
   public DSMPBaseProto readProtocolPacket() throws IOException {
   
      DSMPBaseProto ret = instanceProtocol();
      try {
         ret.readAll(istream);
      } catch(IOException ex) {
         shutdown();
         throw ex;
      }
      return ret;
   }
   
  /*
  ** Schedule a proto packet for transmission
  */
   public void sendProtocolPacket(DSMPBaseProto p) {
//      long ct = System.currentTimeMillis();

     //
     // Try to clear the logjam. Does nothing really, for the DSMPBaseHandler,
     //  but subclasses may do things (like the DSMPHandler) in the process
     //  routine that will remove bytes from our ledger
     //
     // Checking these unsynchronized really does no harm
      if (queuedProtoByteCount + p.memoryFootprint() > maxProtoByteCount/2) {
         processAddInfo();
      }
      
      
     // Then we don't let this guy return until its 
      int complaint = 1;
      synchronized(addLock) {
         while(!done && queuedProtoByteCount >= maxProtoByteCount) {
               
            if ((dispatch == null || dispatch.getMinorDebug())) {
               System.out.println("DSMPBaseHandler: block adding proto[" + 
                                  complaint + "] : " +
                                  p.toString());
               System.out.println("  tosend.size=" + tosend.size());
               System.out.println("  curaddsize =" + 
                                  addInfo.size());
               System.out.println("  queued proto size = " + queuedProtoByteCount);
            }
            complaint++;
            try {
               addLock.wait();
            } catch(InterruptedException ie) {}
            
            if ((dispatch == null || dispatch.getMinorDebug())) {
               System.out.println("DSMPBaseHandler: Awake from block adding proto");
            }
         }         
         
        // We add it regardless of size issues
         queuedProtoByteCount += p.memoryFootprint();
         p.setCurrentTime();
         
         addInfo.addElement(p);
         
        // Keep the stats regarding 
         if (!addInfoHasNormalPriority) {
            addInfoHasNormalPriority = !p.getLowPriority();
         }
         
         addLock.notifyAll();
         startWriting();
      }
      
      
     /*
      long cta = System.currentTimeMillis();
      Integer myquad = p.getVirtualQuadrant();
      String ss = "NA";
      if (myquad != null) {
         ss=myquad.toString();
      }
      System.out.println("AddPacket Delta = " + (cta-ct) + " [" + ss + "]");
     */
   }
   
  /*
  ** processAddInfo 
  **
  **  Consumes packets from addInfo and places them on tosend vector, which 
  **  is where we pull things off to actually send (in getNextProtoPacket).
  **
  **  This is more interesting in some subclasses (DSMPHandler). Also,
  **  addLock (legacy, could use addInfo directly now) being separate from 
  **  tosend keeps writer and protogenerator from butting heads as frequently
  **
  **  JMC 7/21/04 - Low Priorty and non-low priority logic added. This allows
  **                Proto objects where order does not matter, and are 
  **                volumenous to be pushed to the back of the Q (e.g. upload
  **                and download packets for Dropbox).
  */
   protected void processAddInfo() {
     // Get new list of things to add to tosend
      
      Enumeration enum=null;
      synchronized (addLock) {
         if (addInfo.size() != 0) {
            enum=((Vector)addInfo.clone()).elements();
            addInfo.removeAllElements();
            addInfoHasNormalPriority = false;
         }
      }
            
     // Process items going to tosend   
      if (enum != null) {
      
        // tosend is already ordered such that lowPriority items are at the
        //  end of the vec. Low Priority items MUST stay in order, normal
        //  priority items can be scehduled in front of lowPriority items, 
        //  but must NOT be scehduled in front of other normal priority 
        //  items.
         
         synchronized(tosend) {
            int insertPoint = -1;
            while(enum.hasMoreElements()) {
               DSMPBaseProto p = (DSMPBaseProto)enum.nextElement();
               
              // If its low priority, just append
               if (p.getLowPriority()) {
                  tosend.addElement(p);
                  toSendHasLowPriority = true;
               } else if (insertPoint != -1) {
                  if (DebugPrint.getLevel() >= DebugPrint.INFO4) {
                     int skipped = (tosend.size()-insertPoint);
                     if (skipped > 0) {
                        DebugPrint.printlnd(DebugPrint.INFO4, 
                                            "Proto skipping: " + skipped);
                     }
                  }
                  tosend.insertElementAt(p, insertPoint++);
               } else {
                  insertPoint=tosend.size(); 
                  while(insertPoint > 0) {
                     DSMPBaseProto cp = 
                        (DSMPBaseProto)tosend.elementAt(insertPoint-1);
                     
                    // If we found a normal priority on tosend, then put this
                    //  one AFTER it
                     if (!cp.getLowPriority()) {
                        break;
                     }
                     insertPoint--;
                  }
                  
                  if (DebugPrint.getLevel() >= DebugPrint.INFO4) {
                     int skipped = (tosend.size()-insertPoint);
                     if (skipped > 0) {
                        DebugPrint.printlnd(DebugPrint.INFO4, 
                                            "Proto skipping: " + skipped);
                     }
                  }
                  tosend.insertElementAt(p, insertPoint++);
               }
            }
         }
      }
   }
   
  /*
  ** getNextProtoPacket - Used only by writer thread.
  **
  **
  */ 
   protected DSMPBaseProto getNextProtoPacket() {
   
      DSMPBaseProto ret = null;
      
     // If we have no items in tosend, convert waiting addInfo proto
     //
     // Also, if there are lowPriority items in tosend and non-low priority
     // items in addInfo, processAddInfo will copy items over and order
     // low priority to the tail of tosend
      if (tosend.size() == 0 || 
          (toSendHasLowPriority && addInfoHasNormalPriority)) {
         processAddInfo();
      }
      
      try {
         synchronized(tosend) {
            ret = (DSMPBaseProto)tosend.firstElement();
            tosend.removeElementAt(0);
            if (tosend.size() == 0) {
               toSendHasLowPriority = false;
            } else {
               DSMPBaseProto cp = (DSMPBaseProto)tosend.lastElement();
               toSendHasLowPriority = cp.getLowPriority();
            }
         }
      } catch(Exception eee) {
      }
      
      if (ret != null) {
      
        // possibly free up someone blocked adding protocol
         synchronized(addLock) {
            boolean wasOver = queuedProtoByteCount >= maxProtoByteCount;
            queuedProtoByteCount -= ret.memoryFootprint();
            if (wasOver && queuedProtoByteCount < maxProtoByteCount) {
               addLock.notifyAll();
            }
            
            if (queuedProtoByteCount < 0) {
               System.out.println("DSMPBaseHandler: !!! qdprotoByteCnt = " + 
                                  queuedProtoByteCount + 
                                  " tosend.size=" + tosend.size() + 
                                  " ainfo.size=" + addInfo.size());
               queuedProtoByteCount = 0;
            }
         }
         
         long tt = ret.getDeltaTime();
         if (tt > 500 && (dispatch ==null || dispatch.getMinorDebug())) {
            System.out.println("TimeOnQ = " + tt + " QSize = " + 
                               tosend.size() + " [" + getIdentifier() + "] " + 
                               (new Date().toString()));
         }
      }
      return ret;
   }
   
  /* 
  ** When a resting time comes (no more proto to send), this routine can 
  **  be overridden by subclasses to inject proto/whatever. The base just
  **  flushes
  */
   protected void generateRestProtocol() throws Exception {
      ostream.flush();
   }

  
  /*
  ** The thread associated with this object is charged with handling outgoing
  ** protocol packets (sending them to the other side)
  */
   public void run() {
   
     // dispatch.setMinorDebug(true);
      
      currSendInfo    = NUMSENT_INIT * 100000 * 1000;
      nextSendInfo    = 0;
      numsent         = NUMSENT_INIT;
      
      long ptime      = 0;
      long totproto   = 0;
      
      try {
         while(!done) {
            DSMPBaseProto proto = null;
            
            long ctt = System.currentTimeMillis();
            proto = getNextProtoPacket();
               
            if (done || writethread != Thread.currentThread()) {
               break;
            }
               
            if (proto == null && queuedProtoByteCount <= 0) {
            
              // We have nothing left to send, allow subclasses to generate
              //  any protocol it feels is needed here
//               System.out.println("... BGenerate REST: " + System.currentTimeMillis());
               generateRestProtocol();
//               System.out.println("... AGenerate REST: " + System.currentTimeMillis());
                              
               synchronized(addLock) {
                  if (queuedProtoByteCount <= 0 && !done) {
                     try {
                        long ct=0;
                        if ((dispatch == null || dispatch.getMinorDebug())) {
                           ct = System.currentTimeMillis();
                           System.out.println("... ADDINFO WAIT Processed " + 
                                              totproto + " in " + (ct-ptime));
                        }
                                           
                        addLock.wait();
                        if ((dispatch == null || dispatch.getMinorDebug())) {
                           ptime = System.currentTimeMillis();
                           System.out.println("@@@ ADDINFO AWAKE!  Slept = " + 
                                              (ptime-ct));
                           totproto = 0;
                        }
                     } catch(InterruptedException ex) {}
                  }
               }
            }
            
            if (proto != null && !done) {
               totproto++;
               long pre          = System.currentTimeMillis();
               
              // How bout some debug on send!
               if (dispatch != null && dispatch.getDebug() && hdebuglev > 2) {
                  System.out.println(getIdentifier() + " =>");
                  dispatch.checkProtocol(proto);
               }
               
               proto.write(ostream);
               
               bytesWritten += proto.size();
               
               long cta = System.currentTimeMillis();
               if ((dispatch == null || dispatch.getMinorDebug())) {
                  System.out.println("WritePacket: " + 
                                     (proto.memoryFootprint()/1024) + "K = " + 
                                     (cta-ctt));
               }
               
              /* Flush happens prior to wait
               if (tosend.size() == 0 && addInfos[curAddInfo].size() == 0) {
                  System.out.println("Flushing");
                  ostream.flush();
               }
              */
               
              /*
              ** currSendInfo contains  between 8 & 15 send time numbers.
              ** nextSendInfo grows to have 8 samples by the time 
              ** currSendInfo tops out, and the two values are switched
              ** 
              ** So, currSendInfo / (numsent+8) is the Avg bytes/ms
              */
               long post             = System.currentTimeMillis();
               long lastSendTime     = java.lang.Math.max(post-pre, 1);
               long thruput          = proto.size()/lastSendTime;
               currSendInfo += thruput;
               nextSendInfo += thruput;
               if (++numsent >= (NUMSENT_INIT*2)) {
                  currSendInfo = nextSendInfo;
                  nextSendInfo = 0;
                  numsent = NUMSENT_INIT;
//                  System.out.println("AvgSendInfo = " + 
//                                     (currSendInfo/NUMSENT_INIT));
               }
            }
         }
      } catch(IOException ioe) {
         String msg = ioe.getMessage();
         if (msg != null && msg.indexOf("EOF") >= 0) {
            ;
         } else {
            System.out.println("DSMPBaseHandler: run: Shutdown!! Exception: "
                               + ioe.toString() + "\n" + toString());
            ioe.printStackTrace(System.out);
         }
         shutdown();
      } catch(Throwable tt) {
         System.out.println("DSMPBaseHandler: run: Shutdown!! Exception: " + 
                            tt.toString() + "\n" + toString());
         tt.printStackTrace(System.out);
         shutdown();
      }
      
     // Don't synchronize on this
      if (done) {
         writethread = null;
         dispatch.fireShutdownEvent(this);
         closeStreams();
      }
      
      if (verbose) System.out.println("DSMPHandler Done");
   }
   
   public String toString() {
      
      StringBuffer sb = new StringBuffer("DSMPBaseHandler: ");
      
      int wqsz = addInfo.size();
      
      Vector nv = (Vector)tosend.clone();
      int sz = nv.size();
      
      sb.append(getIdentifier()).append(" id[").append(handlerid).append("] ");
      sb.append("\n   AvgWriteThruput=" + (currSendInfo/NUMSENT_INIT));
      sb.append(" Awaiting copy: ").append(wqsz);
      sb.append(" Awaiting write: ").append(sz);
      Enumeration enum = nv.elements();
      while(enum.hasMoreElements()) {
         DSMPBaseProto bp = (DSMPBaseProto)enum.nextElement();
         sb.append("\n      Opcode[").append(bp.getOpcode()).append("]");
         sb.append(" timeonq[").append(bp.getDeltaTime()).append("] ms");
         sb.append(" size[").append(bp.getNonHeaderSize()).append("]");
      }
      return sb.toString();
   }
}
