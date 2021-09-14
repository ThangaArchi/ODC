package oem.edge.ed.odc.tunnel.common;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class TunnelMessageV1 extends TunnelMessage {

   private static final int DELTA_FOR_GOOD_RECORD = 50;
   private static final int HEADER_SIZE = 6;

   public int getOverhead() { return HEADER_SIZE; }
   
   public TunnelMessageV1() {
      reset();
   }
   
   public TunnelMessageV1(byte  eid,  byte iid, 
                          short recn, byte arr[], 
                          int   inlen) {
      super(eid, iid, (short)(recn & 0xff), arr, inlen);
   }
   
   public TunnelMessageV1(byte eid,   byte iid, 
                          short recn, byte arr[], 
                          int inlen,  byte f) {
      super(eid, iid, (short)(recn & 0xff), arr, inlen, f);
   }
   
  // Must case rec to byte here to get proper number
   public String getRecordNumberString() { 
      return "" + (byte)rec;
   }
           
  // Check the old recnum against this recnum. If this rec is 'newer' return
  //  true, otherwise false. Newer here means that within reason, this 'rec'
  //  is not a replay which we have already processed
   public boolean checkValidRecord(short old) {
      boolean ret = false;
      
      if (old > rec) {
         if ( ((256-old) + rec) < DELTA_FOR_GOOD_RECORD ) {
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
         if ( ((256-old) + rec) < DELTA_FOR_GOOD_RECORD ) {
            ret = true;
         }
      } else if (rec == old || (rec-old) < DELTA_FOR_GOOD_RECORD) {
         ret=true;
      }
      return ret;
   }
   
  // Check the old recnum against this recnum. If this rec is 'next', or
  //  old+1 % 256, then true, else false
   public boolean isNextRecord(short old) {
      return rec == ((old+1) & 0xff);
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
     // Old/Original Format      
     // +---+---+----+----+----+----+-----+ ~ +-----+
     // |id1|id2|rec#|len3|len2|len1|Data1| ~ |DataN|
     // +---+---+----+----+----+----+-----+ ~ +-----+
      if (inited) {
         byte h[] = new byte[HEADER_SIZE];
         h[0] = earid;
         h[1] = instid;
         h[2] = (byte)(rec & 0xff);
         h[3] = (byte)((len >> 16) & 0xff);
         h[4] = (byte)((len >>  8) & 0xff);
         h[5] = (byte)((len      ) & 0xff);
         out.write(h);
         
         if (len > 0 && buf != null) {
            out.write(buf, 0, len);
         }
         out.flush();
      }
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
         rec     = (short)(((short)head[2]) & 0xff);
         len     = ((((int)head[3]) << 16)  & 0xff0000) | 
                   ((((int)head[4]) <<  8)  & 0xff00)   | 
                   ((((int)head[5]))        & 0xff); 
         altLen = len;
      }

      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG5,
                            "TM(): readHeader: returning => " + toString());
      }
	
      return inited;
   }
}
