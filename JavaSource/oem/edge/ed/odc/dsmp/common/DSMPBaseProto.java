package oem.edge.ed.odc.dsmp.common;

import java.lang.*;
import java.io.*;
import java.util.*;

public class DSMPBaseProto {

   final static int  HEADER_SIZE   = 6;
   final static byte FLAGS_XTRAINT = (byte)0x80;
   
   protected boolean lowpriority = false;
   protected boolean inited =  false;
   protected byte    opcode =  0;
   protected byte    handle =  0;
   protected byte    flags  =  0;
   protected int     len    =  0;
   protected int     bufofs =  0;
   protected byte    buf[]  =  null;
   protected int     cursor =  0;
   protected int     xtraInt=  0;
   
   protected long    timeOnQ = 0;
   
   protected Vector  listeners = null;
   
   public void setCurrentTime() {
      timeOnQ = System.currentTimeMillis();
   }
   public void setTime(long t) {
      timeOnQ = t;
   }
   public long getTime() {
      return timeOnQ;
   }
   public long getDeltaTime() {
      return  System.currentTimeMillis() - timeOnQ;
   }
   
   public int getNonHeaderSize() { return len; }
   
   public int memoryFootprint() { return 30 + ((buf == null)?0:buf.length); }
                              
   public int size() { return len + HEADER_SIZE + 
                              (((flags & FLAGS_XTRAINT) != 0)?4:0); }
   
   public void resetCursor() { cursor = 0; }
   
   public void setExtraInt(int i) { xtraInt = i;  flags |= FLAGS_XTRAINT; }
   public int  getExtraInt()      { return xtraInt;                       }
   
   public DSMPBaseProto() {
   }
   
   public DSMPBaseProto(byte op, byte f, byte h, byte arr[], int ofs, int l) {
      inited = true;
      opcode = op;
      flags  = f;
      handle = h;
      buf    = arr;
      bufofs = ofs;
      len    = l;
   }
   
   public DSMPBaseProto(byte op, byte f, byte h) {
      inited = true;
      opcode = op;
      flags  = f;
      handle = h;
//      buf    = new byte[16484];
      buf    = new byte[64];
      bufofs = 0;
      len    = 0;
   }
   public DSMPBaseProto(int sz, byte op, byte f, byte h) {
      inited = true;
      opcode = op;
      flags  = f;
      handle = h;
      buf    = new byte[sz];
      bufofs = 0;
      len    = 0;
   }
   
   public void    setLowPriority(boolean v) { lowpriority = v;    }
   public boolean getLowPriority()          { return lowpriority; }
   
   public synchronized void addSentListener(ProtoSentListener v) {
      if (listeners == null) listeners = new Vector(); 
      
      synchronized (listeners) {
         removeSentListener(v);
         listeners.addElement(v);
      }
   }
   public synchronized boolean removeSentListener(ProtoSentListener v) {
      if (listeners != null) {
         synchronized (listeners) {
            int sz = listeners.size();
            for(int i = 0; i < sz; i++) {
               ProtoSentListener x = (ProtoSentListener)listeners.elementAt(i);
               if (x == v) {
                  listeners.removeElementAt(i);
                  return true;
               }
            }
         }
      }
      return false;
   }
   
   void fireProtocolSent() {
      try {
         if (listeners != null) {
            synchronized (listeners) {
               int sz = listeners.size();
               for(int i = 0; i < sz; i++) {
                  ProtoSentListener x = 
                     (ProtoSentListener)listeners.elementAt(i);
                  x.fireSentEvent(this);
               }
            }
         }
      } catch(IndexOutOfBoundsException e) {
      }
   }
   
   public void      addMaskToFlags(int v)      { flags |= v;          }
   public void      removeMaskFromFlags(int v) { flags &= ~v;         }
   public boolean   bitsSetInFlags(int v)      { return (flags&v)==v; }
   
   public void growToFit(int l) {
      if (buf == null) {
         buf = new byte[l + 32];
      } else if (buf.length - len - bufofs < l) {
         byte newbuf[] = new byte[len + l + 32];
         System.arraycopy(buf, bufofs, newbuf, 0, len);
         buf = newbuf;
         bufofs = 0;
      }
   }
   public void appendData(byte inbuf[], int ofs, int l) {
      try {
      growToFit(l);
      System.arraycopy(inbuf, ofs, buf, bufofs+len, l);
      len += l;
      } catch(Exception ee) {
         ee.printStackTrace(System.out);
         System.out.println("inbuf  = "   + inbuf + 
                            " inbufllen=" + inbuf.length +
                            " ofs=" + ofs + " l = " + l);
      }
   }
   
   public void appendByte(byte b) {
      growToFit(1);
      buf[bufofs+len] = b;
      len++;
   }
   
   public void appendShort(int v) {
      growToFit(2);
      int ofs = bufofs+len;
      len += 2;
      buf[ofs++] = (byte)((v >> 8) & 0xff);
      buf[ofs]   = (byte)((v )     & 0xff);
   }
   
   public void append3ByteInteger(int v) {
      growToFit(3);
      int ofs = bufofs+len;
      len += 3;
      buf[ofs++] = (byte)((v >> 16) & 0xff);
      buf[ofs++] = (byte)((v >> 8)  & 0xff);
      buf[ofs]   = (byte)((v )      & 0xff);
   }
   public void appendInteger(int v) {
      growToFit(4);
      int ofs = bufofs+len;
      len += 4;
      buf[ofs++] = (byte)((v >> 24) & 0xff);
      buf[ofs++] = (byte)((v >> 16) & 0xff);
      buf[ofs++] = (byte)((v >> 8)  & 0xff);
      buf[ofs]   = (byte)((v )      & 0xff);
   }
   public void appendLong(long v) {
      growToFit(8);
      int ofs = bufofs+len;
      len += 8;
      buf[ofs++] = (byte)((v >> 56) & 0xff);
      buf[ofs++] = (byte)((v >> 48) & 0xff);
      buf[ofs++] = (byte)((v >> 40) & 0xff);
      buf[ofs++] = (byte)((v >> 32) & 0xff);
      buf[ofs++] = (byte)((v >> 24) & 0xff);
      buf[ofs++] = (byte)((v >> 16) & 0xff);
      buf[ofs++] = (byte)((v >> 8)  & 0xff);
      buf[ofs]   = (byte)((v )      & 0xff);
   }
   
   public void appendString8(String s) {
      if (s != null) {
         byte arr[] = s.getBytes();
         int llen = arr.length & 0xff;
         growToFit(llen + 1);
         appendByte((byte)llen);
         System.arraycopy(arr, 0, buf, bufofs+len, llen);
         len += llen;
      } else {
         growToFit(1);
         appendByte((byte)0);
      }
   }
   public void appendString16(String s) {
      if (s != null) {
         byte arr[] = s.getBytes();
         int llen = arr.length & 0xffff;
         growToFit(llen + 2);
         appendShort(llen);
         System.arraycopy(arr, 0, buf, bufofs+len, llen);
         len += llen;
      } else {
         growToFit(2);
         appendShort(0);
      }
   }
   
   public int cursorBytesLeft() {
      int ret = 0;
      if (cursor < len) ret = len - cursor;
      return ret;
   }
   
   public synchronized byte getByte() throws InvalidProtocolException {
      byte ret = 0;
      try {
         ret = buf[bufofs+cursor];
         cursor += 1;
      } catch (ArrayIndexOutOfBoundsException  e) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": Array exception");
      } catch(NullPointerException ne) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": NullPointerEx");
      }
      return ret;
   }
   
   public synchronized int getUnsignedByte() throws InvalidProtocolException {
      int ret = 0;
      try {
         ret = ((int)buf[bufofs+cursor]) & 0xff;
         cursor += 1;
      } catch (ArrayIndexOutOfBoundsException  e) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": Array exception");
      } catch(NullPointerException ne) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": NullPointerEx");
      }
      return ret;
   }
   
   public synchronized int getUnsignedShort() throws InvalidProtocolException {
      int ret = 0;
      try {
         ret = ((((int)buf[bufofs+cursor])   & 0xff) << 8) | 
               ((((int)buf[bufofs+cursor+1]) & 0xff));
         cursor += 2;
      } catch (ArrayIndexOutOfBoundsException  e) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": Array exception");
      } catch(NullPointerException ne) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": NullPointerEx");
      }
      return ret;
   }
   
   public synchronized short getShort() throws InvalidProtocolException {
      short ret = 0;
      try {
         ret = (short)(((((int)buf[bufofs+cursor])   & 0xff) << 8) | 
                       ((((int)buf[bufofs+cursor+1]) & 0xff)));
         cursor += 2;
      } catch (ArrayIndexOutOfBoundsException  e) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": Array exception");
      } catch(NullPointerException ne) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": NullPointerEx");
      }
      return ret;
   }
   
   public synchronized int get3ByteInteger() throws InvalidProtocolException {
      int ret = 0;
      try {
         ret = ((((int)buf[bufofs+cursor])   & 0xff) << 16) | 
               ((((int)buf[bufofs+cursor+1]) & 0xff) << 8)  | 
               ((((int)buf[bufofs+cursor+2]) & 0xff));
         cursor += 3;
      } catch (ArrayIndexOutOfBoundsException  e) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": Array exception");
      } catch(NullPointerException ne) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": NullPointerEx");
      }
      return ret;
   }
   
   public synchronized int getInteger() throws InvalidProtocolException {
      int ret = 0;
      try {
         ret = ((((int)buf[bufofs+cursor])   & 0xff) << 24) | 
               ((((int)buf[bufofs+cursor+1]) & 0xff) << 16) | 
               ((((int)buf[bufofs+cursor+2]) & 0xff) << 8)  | 
               ((((int)buf[bufofs+cursor+3]) & 0xff));
         cursor += 4;
      } catch (ArrayIndexOutOfBoundsException  e) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": Array exception");
      } catch(NullPointerException ne) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": NullPointerEx");
      }
      return ret;
   }
   
   public synchronized long getLong() throws InvalidProtocolException {
      long ret = 0;
      try {
         ret = ((((long)buf[bufofs+cursor])   & 0xff) << 56) | 
               ((((long)buf[bufofs+cursor+1]) & 0xff) << 48) | 
               ((((long)buf[bufofs+cursor+2]) & 0xff) << 40) | 
               ((((long)buf[bufofs+cursor+3]) & 0xff) << 32) | 
               ((((long)buf[bufofs+cursor+4]) & 0xff) << 24) | 
               ((((long)buf[bufofs+cursor+5]) & 0xff) << 16) | 
               ((((long)buf[bufofs+cursor+6]) & 0xff) << 8)  | 
               ((((long)buf[bufofs+cursor+7]) & 0xff));
               
        /*
          System.out.println("getLong: ret=" + ret + 
          " bufofs=" + bufofs + 
          " cursor=" + cursor + "  " + 
          DSMPDispatchBase.showbytes(buf, bufofs+cursor, 8));
        */
        
         cursor += 8;
      } catch (ArrayIndexOutOfBoundsException  e) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": Array exception");
      } catch(NullPointerException ne) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": NullPointerEx");
      }
      return ret;
   }
   
   public synchronized String getString8() throws InvalidProtocolException {
      String ret = null;
      int l = getUnsignedByte();
      try {
         ret = new String(buf, bufofs + cursor, l);
         cursor += l;
      } catch (ArrayIndexOutOfBoundsException  e) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": Array exception l=" + l);
      } catch(NullPointerException ne) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": NullPointerEx");
      }
      return ret;
   }
   public synchronized String getString16() throws InvalidProtocolException {
      String ret = null;
      int l = getShort();
      try {
         ret = new String(buf, bufofs + cursor, l);
         cursor += l;
      } catch (ArrayIndexOutOfBoundsException  e) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": Array exception l=" + l);
      } catch(NullPointerException ne) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": NullPointerEx");
      }
      return ret;
   }
   
   public synchronized  CompressInfo getDataAtCursor(int l) 
                                throws InvalidProtocolException {
      CompressInfo ret = null;
      try {
         byte lastbyte = buf[bufofs+cursor+l-1];
         ret = new CompressInfo(buf, bufofs+cursor, l);
         cursor += l;
      } catch (ArrayIndexOutOfBoundsException  e) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": Array exception");
      } catch(NullPointerException ne) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": NullPointerEx");
      }
      return ret;
   }
   
   public synchronized CompressInfo getDataAtCursor() 
                                 throws InvalidProtocolException {
      CompressInfo ret = null;
      try {
         int l = len-cursor;
         if (l <= 0) {
            throw new InvalidProtocolException("Op = " + opcode + 
                                               ": Array exception");
         }
         ret = new CompressInfo(buf, bufofs+cursor, l);
         cursor += l;
      } catch (ArrayIndexOutOfBoundsException  e) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": Array exception");
      } catch(NullPointerException ne) {
         throw new InvalidProtocolException("Op = " + opcode + 
                                            ": NullPointerEx");
      }
      return ret;
   }
   
   public synchronized void verifyCursorDone() 
                                        throws InvalidProtocolException {
      int leftover = cursorBytesLeft();
      if (leftover != 0) {
         throw new InvalidProtocolException(opcode + ": Leftover Bytes = " + 
                                            leftover);
      }
   }
   
   public void setData(byte inbuf[], int ofs, int l) {
       bufofs = ofs;
       len    = l;
       buf    = inbuf;
   }
   public void setData(byte inbuf[]) {
      bufofs = 0;
      len    = inbuf.length;
      buf    = inbuf;
   }
   
   public synchronized int getDataLength() {
      return len;
   }
   
   public synchronized CompressInfo getData() {
      CompressInfo ret = new CompressInfo(buf, bufofs, len);
      return ret;
   }
   
   public boolean getInited() {
      return inited;
   }
   
   public void setInited(boolean v) {
      inited = v;
   }
   
   public byte getOpcode() {
      return opcode;
   }
   public void setOpcode(byte o) {
      opcode = o;
   }
   public byte getHandle() {
      return handle;
   }
   public void setHandle(byte h) {
      handle = h;
   }
   public byte getFlags() {
      return flags;
   }
   public void setFlags(byte f) {
      flags = f;
   }
      
   public void write(OutputStream out) throws IOException {
     /*
     **                                            DATA portion
     **                                       .____________________. 
     **        1Byte   1Byte  3Bytes  1Byte  /                      \
     **      +-------+-------+-------+------+-------+-------+-------+
     **      |OPCODE | FLAGS | LENP  | HAND | B1    |  ...  | BLENP |  
     **      +-------+-------+-------+------+-------+-------+-------+
     **
     **
     **   If the flag bit FLAGS_XTRAINT is set (0x80) then an integer value
     **    is stuffed between HAND and B1  (effectively a 10 byte header)
     */
      if (inited) {
         boolean xtra = bitsSetInFlags(FLAGS_XTRAINT);
         byte h[] = new byte[HEADER_SIZE + (xtra?4:0)];
         h[0] = opcode;
         h[1] = flags;
         h[2] = (byte)((len >>  16) & 0xff);
         h[3] = (byte)((len >>  8)  & 0xff);
         h[4] = (byte)((len      )  & 0xff);
         h[5] = handle;
         if (xtra) {
            h[6] = (byte)((xtraInt >>  24) & 0xff);
            h[7] = (byte)((xtraInt >>  16) & 0xff);
            h[8] = (byte)((xtraInt >>  8)  & 0xff);
            h[9] = (byte)((xtraInt      )  & 0xff);
         }
         out.write(h);
         
         if (len > 0 && buf != null) {
            out.write(buf, bufofs, len);
         }
         fireProtocolSent();
      }
   }
        
        
  // Read protocol from an array ... all return updated ofs
   public int readAll(byte arr[], int ofs, int l) {
      ofs=readHeader(arr, ofs, l);
      if (ofs >= 0) {
         ofs = readBody(arr, ofs, l);
      }
      return ofs;
   }
   
   public int readHeader(byte arr[], int ofs, int l) {
      reset();
      return setHeader(arr, ofs, l);
   }
   
   public int readBody(byte arr[], int ofs, int l) {
      int totlen = 0;
      buf = new byte[len];
      bufofs = 0;
      System.arraycopy(arr, ofs, buf, 0, len);
      return ofs + len;
   }
   
   
        
  // Read protocol from an InputStream ... all return updated true if worked,
  //  exception otherwise
   public boolean readAll(InputStream tin) throws IOException {
      if (readHeader(tin)) {
         readBody(tin);
      }
      return inited;
   }
   public boolean readBody(InputStream tin) throws IOException {
      int totlen = 0;
      buf = new byte[len];
	
      try {
         while (totlen != len) {
//            System.out.println("ReadBody: Tot = " + len + " cur = " + totlen);
            int l = tin.read(buf, totlen, len - totlen);
//            System.out.println("        : Read = " + l);
            if (l == -1) {
               inited = false;
               throw new IOException("DSMPProto: readBody: EOF");
            }
            totlen += l;
         }
      } catch (IOException e) {
         inited = false;
         throw e;
      }
      
      return inited;
   }
   
   public boolean readHeader(InputStream tin) throws IOException {
      int totlen = 0;
      reset();
      byte head[] = new byte[HEADER_SIZE+4];
      try {
         int totreadsz = HEADER_SIZE;
         while (totlen != totreadsz) {
            
//            System.out.println("ReadHead: Tot = " + totreadsz + " cur = " + totlen);
            int l = tin.read(head, totlen, totreadsz - totlen);
//            System.out.println("        : Read = " + l);
            if (l == -1) {
               throw new IOException("DSMPProto: readHeader: EOF");
            }
            
            totlen += l;
            
           // If the extraint flag is set, get the extra int
            if (totlen == HEADER_SIZE && (head[1] & FLAGS_XTRAINT) != 0) {
               totreadsz = HEADER_SIZE+4;
            }
         }
         setHeader(head, 0, totreadsz);
      } catch (IOException e) {
         inited = false;
         throw e;
      }
      
      return inited;
   }
   
   public int setHeader(byte head[], int ofs, int l) {
      inited = true;
      
      opcode  = head[ofs+0];
      flags   = head[ofs+1];
      len = ((((int)head[ofs+2]) << 16) & 0xff0000) | 
            ((((int)head[ofs+3]) <<  8) & 0xff00)   | 
            ((((int)head[ofs+4]))       & 0xff); 
      handle  = head[ofs+5];
      if ((flags & FLAGS_XTRAINT) != 0) {
         xtraInt = ((((int)head[ofs+6]) << 24) & 0xff000000) | 
                   ((((int)head[ofs+7]) << 16) & 0xff0000)   | 
                   ((((int)head[ofs+8]) <<  8) & 0xff00)     | 
                   ((((int)head[ofs+9]))       & 0xff); 
         ofs+=4;
      }
      ofs += HEADER_SIZE;
      return ofs;
   }
   
   public void reset() {
      opcode   = 0;
      handle   = 0;
      flags    = 0;
      len      = 0;
      bufofs   = 0;
      buf      = null;
      inited   = false;
      xtraInt  = 0;
      cursor   = 0;
   }
   
   public String toString() {
      String ret = 
         "DSMProto inited=" + inited + 
         " opcode="  + opcode +
         " flags="   + flags  + 
         " datalen=" + len    +
         " handle="  + handle +
         " cursor="  + cursor +
         " xtraint=" + xtraInt +
         " footprint=" + memoryFootprint();
         
      if (len > 0) {
         int dolen = len < 256 ? len : 256;
         ret += "\n" + DSMPDispatchBase.showbytes(buf, bufofs, dolen);
      }
      return ret;
   }
}
