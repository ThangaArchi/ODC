package oem.edge.ed.odc.meeting.common;

import  oem.edge.ed.odc.dsmp.common.*;

import java.lang.*;
import java.io.*;
import java.util.*;

public class DSMPProto extends DSMPBaseProto {

   protected Integer vq     = null;
   
   public DSMPProto() {
      super();
   }
   
   public DSMPProto(byte op, byte f, byte h, byte arr[], int ofs, int l) {
      super(op, f, h, arr, ofs, l);
   }
   
   public DSMPProto(byte op, byte f, byte h) {
      super(op, f, h);
   }
   
   public DSMPProto(int sz, byte op, byte f, byte h) {
      super(sz, op, f, h);
   }
   
   public void reset() {
      super.reset();
      vq       = null;
   }
   
  // 150 QUADMAX handles 9600 pels if block size is 64
   static final int QUADMAX      = 150;
   static final int QUADTOTSIZE  = (QUADMAX*QUADMAX);
   static final int BLOCK_WIDTH  = 64;
   static final int BLOCK_HEIGHT = 64;
   public synchronized Integer getVirtualQuadrant() {
      if (vq == null && 
          (opcode == DSMPGenerator.OP_FRAMEUPDATE ||
           opcode == DSMPGenerator.OP_FRAMEUPDATE_EVENT)) {
         int precursor = cursor;
         cursor = 0;
         try {
            int x = getShort();
            int y = getShort();
//            System.out.println("X = " + x + " Y = " + y);
            int tvq = (x/BLOCK_WIDTH) + (QUADMAX*(y/BLOCK_HEIGHT));
            vq = new Integer(tvq);
         } catch(InvalidProtocolException ii) {}
         cursor = precursor;
      }
      return vq;
   }
}
