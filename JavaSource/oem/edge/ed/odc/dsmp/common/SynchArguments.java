package oem.edge.ed.odc.dsmp.common;

import java.util.*;

public class SynchArguments extends Vector {

   public SynchArguments(DSMPBaseHandler h, DSMPBaseProto p) {
      proto   = p;
      handler = h;
   }

   public DSMPBaseProto     proto = null;
   public DSMPBaseHandler handler = null;
   
   protected int cursor = 0;
   public    void resetCursor() { cursor = 0; }
   
      
   public boolean booleanAt(int v) {
      return ((Boolean)elementAt(v)).booleanValue();
   }
   public int         intAt(int v) {
      return ((Integer)elementAt(v)).intValue();
   }
   public short     shortAt(int v) {
      return   ((Short)elementAt(v)).shortValue();
   }
   public long       longAt(int v) {
      return ((Long)elementAt(v)).longValue();
   }
   public String   stringAt(int v) {
      return (String)elementAt(v);
   }
   public Object   objectAt(int v) {
      return elementAt(v);
   }
   
   public boolean nextBoolean() {
      return ((Boolean)elementAt(cursor++)).booleanValue();
   }
   public int         nextInt() {
      return ((Integer)elementAt(cursor++)).intValue();
   }
   public short     nextShort() {
      return   ((Short)elementAt(cursor++)).shortValue();
   }
   public long       nextLong() {
      return ((Long)elementAt(cursor++)).longValue();
   }
   public String   nextString() {
      return (String)elementAt(cursor++);
   }
   public Object   nextObject() {
      return elementAt(cursor++);
   }
   
}
