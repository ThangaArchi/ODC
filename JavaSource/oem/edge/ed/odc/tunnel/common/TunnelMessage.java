package oem.edge.ed.odc.tunnel.common;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class TunnelMessage {

   protected byte    earid;
   protected byte    instid;
   protected short   rec;
   protected int     len;
   protected int     altLen;
   protected byte    buf[];
   protected boolean inited;
   protected byte    flags;
   
  // Delta set to 200 means that we can have up to 500 tunnel messages
  //  sent out of order, ahead of the next expected record.  With 4096 
  //  possible rec#'s, wrapping is no longer a problem.
   public static final int DELTA_FOR_GOOD_RECORD = 500;
   public static final int HEADER_SIZE = 7;

   public int getOverhead() { return HEADER_SIZE; }
   
   public TunnelMessage() {
      reset();
   }
   
   public TunnelMessage(byte  eid,  byte iid, 
                        short recn, byte arr[], 
                        int   inlen) {
      inited = true;
      earid = eid;
      instid = iid;
      rec = recn;
      buf = arr;
      len = altLen = inlen;
      flags = 0;
   }
   
   public TunnelMessage(byte eid,   byte iid, 
                        short recn, byte arr[], 
                        int inlen,  byte f) {
      inited = true;
      earid = eid;
      instid = iid;
      rec = recn;
      buf = arr;
      len = altLen = inlen;
      flags = f;
   }
   
   public byte getFlags() {
      return flags;
   }
   public void setFlags(byte f) {
      flags = f;
   }
   
   public void    isCompressed(boolean v) {
      flags |= 1;
   }
   public boolean isCompressed() {
      return (flags & 1) != 0;
   }
   
   public int decompress(Inflater inflater) 
                                     throws java.util.zip.DataFormatException {
      int num = len;
      if (isCompressed() && len > 0) {
      
         inflater.reset();
         inflater.setInput(buf, 0, len);
      
         num = 0;
         byte debuf[] = new byte[len > 66666?200000:(len*3)];
         while(!inflater.needsInput()) {
            if (num == debuf.length) {
               if (DebugPrint.getLevel() > DebugPrint.INFO3) {
                  DebugPrint.println(DebugPrint.INFO4,
                     "TM: decompress: debuf.length must be bumped up! clen=" +
                     len + " dlen=" + num);
               }
                  
               byte tt[] = new byte[debuf.length*2 + 1];
               System.arraycopy(debuf, 0, tt, 0, num);
               debuf = tt;
            }
            int lnum = inflater.inflate(debuf, num, debuf.length-num);
            
            if (lnum > 0) {
               num += lnum;
            } else {
               break;
            }
         }
         
         if (num > 0) {
            buf = debuf;
            altLen = len;
            len = num;
         } else {
            DebugPrint.printlnd(DebugPrint.WARN, "Decompress of " + len +
                               " bytes yielded " + num + " bytes!");
            DebugPrint.println(DebugPrint.WARN, "needsInput = " + 
                               inflater.needsInput());
            DebugPrint.println(DebugPrint.WARN, "finished = " + 
                               inflater.finished());
            DebugPrint.println(DebugPrint.WARN, "needsDict = " + 
                               inflater.needsDictionary());
            DebugPrint.println(DebugPrint.WARN, "totIn = " + 
                               inflater.getTotalIn() + " totOut = " +
                               inflater.getTotalOut());
            DebugPrint.println(DebugPrint.WARN, "remaining = " + 
                               inflater.getRemaining());
           /*
            try {
               java.io.FileOutputStream os = 
                  new java.io.FileOutputStream("/tmp/errout");
               os.write(buf, 0, len);
               os.close();
               DebugPrint.println(DebugPrint.WARN, 
                                  "Check /tmp/errout for compressed data");
            } catch(IOException ioe) {
               DebugPrint.println(DebugPrint.WARN, 
                                  "Error writing /tmp/errout");
               DebugPrint.println(DebugPrint.WARN, ioe);
            }
           */
         }
      }
      return num;
   }
   
   public byte[] getBuffer() {
      return buf;
   }
   public byte getEarId() {
      return earid;
   }
   public byte getInstId() {
      return instid;
   }
   public int getLength() {
      return len;
   }
   public int getAlternateLength() {
      return altLen;
   }
   public void setAlternateLength(int l) {
      altLen = l;
   }
   public short getRecordNumber() {
      return rec;
   }
   
   public String getRecordNumberString() { 
      return "" + rec;
   }
        
        
  // Check the old recnum against this recnum. If this rec is 'newer' return
  //  true, otherwise false. Newer here means that within reason, this 'rec'
  //  is not a replay which we have already processed
   public boolean checkValidRecord(short old) {
      boolean ret = false;
      
      if (old > rec) {
         if ( ((4096-old) + rec) < DELTA_FOR_GOOD_RECORD ) {
            ret = true;
         }
      } else if (rec > old && (rec-old) < DELTA_FOR_GOOD_RECORD) {
         ret=true;
      }
      return ret;
   }
   public boolean checkValidRecordOrNext(short old) {
      boolean ret = false;
      
      if (old > rec) {
         if ( ((4096-old) + rec) < DELTA_FOR_GOOD_RECORD ) {
            ret = true;
         }
      } else if (rec == old || (rec-old) < DELTA_FOR_GOOD_RECORD) {
         ret=true;
      }
      return ret;
   }
   
  // Check the old recnum against this recnum. If this rec is 'next', or
  //  old+1 % 4096, then true, else false
   public boolean isNextRecord(short old) {
      return rec == ((old+1) & 0xfff);
   }
   
   public void write(OutputStream out) throws IOException {
     //
     // When we write to the OutBuf, we must give an earID (id1) 
     //   and instanceID (id2) to help demuxing on other side. 
     //
     // The rec parm help keep the sequence if multiple channels 
     //   are used
     //
     // The lenx parms are Bigendian length of data to follow. This 
     //   needed in streaming case. (perhaps use only len1/len2)
     //
     //
     // Len3 and Rec2 are housed in the same byte. Each take 4 bits. This
     //  gives recnum 4096 uniq values, while len has top val of 1 Meg.
     //
     // New Format as of 3/29/02
     // +---+---+---+----+----+----+----+----+-----+ ~ +-------+
     // |id1|id2|flg|rec1|rec2-len3|len2|len1|Data1| ~ | DataN |
     // +---+---+---+----+----+----+----+----+-----+ ~ +-------+
     //
     // Old/Original Format      
     // +---+---+----+----+----+----+-----+ ~ +-----+
     // |id1|id2|rec#|len3|len2|len1|Data1| ~ |DataN|
     // +---+---+----+----+----+----+-----+ ~ +-----+
      if (inited) {
         byte h[] = new byte[HEADER_SIZE];
         h[0] = earid;
         h[1] = instid;
         h[2] = flags;
         h[3] = (byte)(rec & 0xff);
         h[4] = (byte)(((byte)((rec >>4)   & 0xf0)) | 
                       ((byte)((len >> 16) & 0x0f)));
         h[5] = (byte)((len >>  8) & 0xff);
         h[6] = (byte)((len      ) & 0xff);
         out.write(h);
         
         if (len > 0 && buf != null) {
            out.write(buf, 0, len);
         }
         out.flush();
      }
   }
        
   public boolean readAll(InputStream tin) {
      if (readHeader(tin)) {
         readBody(tin);
      }
      return inited;
   }
   public boolean readBody(InputStream tin) {
      int totlen = 0;
      buf = new byte[len];
	
      try {
         while (totlen != len) {
           
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG5,
                                  "TM(): About to read");
            }
              
            int l = tin.read(buf, totlen, len - totlen);
            if (l == -1) {
              
               if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG5,
                                     "TM(): Gak! -1 while " + 
                                     "reading body after " + 
                                     totlen + " bytes");
               }
                 
               inited = false;
               break;
            }
              
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG5,
                                  "TM(): After read");
            }
              
            totlen += l;
         }
      } catch (Exception e) {
         e.printStackTrace(System.out);
         inited = false;
      }

      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG5,
                            "readBody: Returning =>" + toString());
      }
        
      return inited;
   }
   public boolean readHeader(InputStream tin) {
      int totlen = 0;
      reset();
      inited = true;
      byte head[] = new byte[HEADER_SIZE];
      try {
         while (totlen != HEADER_SIZE) {
         
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG5,
                                  "TM(): About to read");
            }
            
            int l = tin.read(head, totlen, HEADER_SIZE - totlen);
            if (l == -1) {
            
               if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG5,
                     "TM(): Gak! -1 while reading head after " + 
                     totlen + " bytes");
               }
               
               inited = false;
               break;
            }
            
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG5, "TM(): After read");
            }
            
            totlen += l;
         }
      } catch (Exception e) {
         e.printStackTrace(System.out);
         inited = false;
      }
	
      if (inited) {
         earid   = head[0];  // earid
         instid  = head[1];  // instid
         flags   = head[2];
         rec     = (short)((((short)head[3])       & 0xff)    |
                          ((((short)head[4]) << 4) & 0x0f00)); 
         len = ((((int)head[4]) << 16) & 0x0f0000) | 
               ((((int)head[5]) <<  8) & 0xff00)   | 
               ((((int)head[6]))       & 0xff); 
         altLen = len;
      }

      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG5,
                            "TM(): readHeader: returning => " + toString());
      }
	
      return inited;
   }
   public void reset() {
      earid = instid = 0;
      rec = 0;
      altLen = len = 0;
      buf = null;
      inited = false;
      flags = 0;
   }
        
   public boolean equals(Object o) {
      if (o instanceof TunnelMessage) {
         TunnelMessage to = (TunnelMessage)o;
      
         if (to.getEarId()        == earid  &&
             to.getInstId()       == instid &&
             to.getRecordNumber() == rec) {
            return true;
         }
      }
   
      return false;    
   }

   public String toString() {
      return "Inited=" + inited + " earid=" + earid + " instid=" + instid + " rec=" + rec + " len=" + len + " altLen=" + altLen + " flags=" + flags;
   }
}
