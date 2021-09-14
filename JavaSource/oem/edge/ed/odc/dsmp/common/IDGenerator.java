package oem.edge.ed.odc.dsmp.common;

import java.lang.*;
public class IDGenerator {
   private static Integer sync = new Integer(1);
   private static int uniq  = 1;
   static public int getId() {
      synchronized (sync) {
         return uniq++;
      }
   }
}
