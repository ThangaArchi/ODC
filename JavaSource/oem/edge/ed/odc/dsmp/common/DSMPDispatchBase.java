package oem.edge.ed.odc.dsmp.common;

import java.util.*;
import java.util.zip.*;

public abstract class DSMPDispatchBase {

   public  static final boolean printdebug   = true;
   protected boolean dodebug                 = false;
   protected boolean redispatch              = true;
   public    DSMPDispatchBase pdbg           = null;
   
   protected boolean debugminor              = false;
   public boolean getMinorDebug() { return debugminor; }
   public void setMinorDebug(boolean v) { debugminor = v; }
   
   protected Deflater deflater = null;
   protected Inflater inflater = null;
   
   protected byte synchHandle       = (byte)1;
   protected Hashtable waitingReply = new Hashtable();
   
   public    void setRedispatch(boolean v) { redispatch = v; }
   
   public boolean getDebug() { return dodebug; }
   public void setDebug(boolean v) {
      dodebug = v;
      cpress.setDebug(v);
      
     // Flip debugminor. This is a cheapskate way of getting another
     //  debug option on a panel. Sigh. Only do it if we are redispatching
     //  (ie. not pdbg).  Also, print out some memory stats when debug is
     //  flipped on.
      if (v && redispatch) {
         
         debugminor = !debugminor;
         System.out.println("Current Java Heap use statistics:");
         Runtime rt = Runtime.getRuntime();
         long tm = rt.totalMemory();
         long fm = rt.freeMemory();
         System.out.println("   preGC  MemoryUsage Total: " + tm + 
                            " Free: "  + fm + 
                            " Used: "  + (tm-fm));
         rt.gc();
         long ptm = rt.totalMemory();
         long pfm = rt.freeMemory();
         System.out.println("   postGC  MemoryUsage Total: " + ptm + 
                            " Free: "  + pfm + 
                            " Used: "  + (ptm-pfm));
         
         System.out.println("   delta   MemoryUsage Total: " + (ptm-tm) + 
                            " Free: "  + (pfm-fm) + 
                            " Used: "  + ((ptm-pfm)-(tm-fm)));
      }
   }
      
   protected Compression cpress = new Compression();
   
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
         if (modv == 8) ret.append("  ");
         modv++;
      }
      
      ret.append(tail.toString()).append("*");
      
      return ret.toString();
   }
      
   public void notifySenders() {
      synchronized(waitingReply) {
         waitingReply.notifyAll();
      }
   }
   
   public byte getReplyForOpcode(byte op) throws InvalidProtocolException {
      throw new InvalidProtocolException(
         "getReplyForOpcode MUST be overridden");
   }
   
  // Note, the handle set in the incoming protocol is IGNORED!
   public SynchArguments sendWithReply(DSMPBaseHandler handler, 
                                       DSMPBaseProto p) 
      throws InvalidProtocolException {
      return sendWithReplyI(handler, p, false);
   }                                       
   
  // Note, the handle set in the incoming protocol is IGNORED!
  // Was having problems where calls coming in on Reader thread were 
  // blocking ;-) Did not need the replies anyway in those cases.
   public void sendIgnoreReply(DSMPBaseHandler handler, 
                               DSMPBaseProto p) 
      throws InvalidProtocolException {
      sendWithReplyI(handler, p, true);
   }                                       
   
  // Note, the handle set in the incoming protocol is IGNORED!
  // If ignoreReply is TRUE, then the matching reply is just chucked
   public SynchArguments sendWithReplyI(DSMPBaseHandler handler, 
                                        DSMPBaseProto p, 
                                        boolean ignoreReply) 
      throws InvalidProtocolException {
      
      SynchArguments ret = null;
      byte enterOp = p.getOpcode();
      byte replyOp = getReplyForOpcode(enterOp);
      
      if (handler != null && !handler.eof()) {
         
         synchronized (waitingReply) {
            
            Byte        b = null;
            boolean found = false;
            
            if (synchHandle == 0) synchHandle++;
            byte synchHandleStart = synchHandle;
            
           // Loop forever (if not ignoring reply) or until we find a free hand
            while(!ignoreReply) {
              // Allocate a free handle
               if (++synchHandle == 0) synchHandle++;
               
               b = new Byte(synchHandle);
               if (waitingReply.get(b) == null) {
                  break;
               }
               
              // Youch! all handles are in use. Should not be true unless there
              //  is a BIG prob. If there is 1 thread doing business, then only
              //  one handle should be in use at a time!
               if (synchHandle == synchHandleStart) {
                  throw new InvalidProtocolException("No Free Handles!");
               }
            }
            
           // Set the SYNCH bit
            p.addMaskToFlags(0x40);
            
           // If we are ignoring the reply, just set the IGNORE handle of 0, 
           //  submit the proto for sending, and return
            if (ignoreReply) {
               p.setHandle((byte)0);
               handler.sendProtocolPacket(p);
               return null;
            }
            
           // Set the handle to be what we need
            p.setHandle(b.byteValue());
            waitingReply.put(b, p);
            
            handler.sendProtocolPacket(p);
            
            while(!handler.eof()) {
            
               Object obj = waitingReply.get(b);
               
               if (obj == null) {
                  System.out.println("sendWithReply: Saved Proto obj is NULL!");
                  break;
               }
               
               if (obj instanceof DSMPBaseProto) {
                  DSMPBaseProto tp = (DSMPBaseProto)obj;
                  if (tp.getOpcode() != enterOp) {
                     System.out.println(
                        "Zoinks! Saved op is != than what we sent! " + enterOp);
                     System.out.println(tp.toString());
                  }
               } else if (obj instanceof SynchArguments) {
                  SynchArguments sargs = (SynchArguments)obj;
                  if (sargs.proto.getOpcode() != replyOp) {
                     System.out.println(
                        "Zoinks! reply op is != than what we wanted!");
                     System.out.println("IN op = "        + enterOp + 
                                        " Expected op = " + replyOp +
                                        " Got\n\t" + sargs.proto.toString());
                     break;
                  }
                  ret = sargs;
                  waitingReply.remove(b);
                  break;
               } else {
                  System.out.println(
                     "Zoinks! unknown type in waitingReply hash: " + 
                     obj.getClass().toString());
                  break;
               }
               
               try { 
//                     System.out.println("Waiting for reply");
                  waitingReply.wait();
               } catch(InterruptedException ee) {}
            }
         }
      }         
      
     /*
      if (ret != null) {
         System.out.println("sendWithReply: returning: " + ret.toString());
      } else {
         System.out.println("sendWithReply: RET is NULL! " + handler.eof());
      }
     */
     
      if (ret == null) {
         throw new InvalidProtocolException("No return from sendWithReply");
      }
     
      return ret;
   }
   
   protected void handleSynchProto(SynchArguments sargs) {
   
      byte opcode  = sargs.proto.getOpcode();
      byte handle  = sargs.proto.getHandle();
      
     // If uses the 0 handle, then we just ignore it
      if (handle == (byte)0) return;
      
      Byte handleObj = new Byte(handle);
      Object sento = waitingReply.get(handleObj);
      if (sento != null) {
         if (!(sento instanceof DSMPBaseProto)) {
            System.out.println("Ugg. Got proto op = " + 
                               opcode + " with handle " + handle + 
                               " already answer waiting!!");
         }
         DSMPBaseProto sentp = (DSMPBaseProto)sento;
//            System.out.println("Received::\n" + proto);
         
         waitingReply.put(handleObj, sargs);
         notifySenders();
      } else {
         System.out.println("Ugg. Got proto op = " + 
                            opcode + " with handle " + handle + 
                            " and NOOne waiting for it!!");
      }
   }
   
   
  /* -------------------------------------------------------*\
  ** Shutdown
  \* -------------------------------------------------------*/
   public void fireShutdownEvent(DSMPBaseHandler h) {
      System.out.println("-----fireShutdownEvent connectionid = " + 
                         h.getHandlerId());
   }
   
   public void uncaughtProtocol(DSMPBaseHandler h, byte opcode) {
      ;
   }

  /* -------------------------------------------------------*\
  ** Commands
  \* -------------------------------------------------------*/
   
  /* -------------------------------------------------------*\
  ** Replies
  \* -------------------------------------------------------*/
   
  /* -------------------------------------------------------*\
  ** Reply Errors
  \* -------------------------------------------------------*/
   
  /* -------------------------------------------------------*\
  ** Events
  \* -------------------------------------------------------*/
   
  /* -------------------------------------------------------*\
  ** Errors
  \* -------------------------------------------------------*/
   
  /* -------------------------------------------------------*\
  ** Checking and Dispatching
  \* -------------------------------------------------------*/
  
   public void checkProtocol(DSMPBaseProto proto) 
                                         throws InvalidProtocolException {
      dispatchProtocolI(proto, null, false);
   }
   
   public void dispatchProtocol(DSMPBaseProto proto, DSMPBaseHandler h) 
                                      throws InvalidProtocolException {
      dispatchProtocolI(proto, h, true);
   }
   
   public abstract void dispatchProtocolI(DSMPBaseProto proto, 
                                          DSMPBaseHandler handler, 
                                          boolean doDispatch) 
                                throws InvalidProtocolException;
                                            
}
