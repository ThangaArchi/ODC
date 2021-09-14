package oem.edge.ed.odc.dsmp.common;
import java.lang.reflect.*;

public class CompressInfo {
   public byte[] buf;
   public int ofs;
   public int len;
   CompressInfo(byte[] arr, int ofs, int len) {
      buf = arr;
      this.ofs = ofs;
      this.len = len;
   }
   
   public String toString() {
      return "CInfo: ofs = " + ofs + " len = " + len;
   }
   
   
   static public void dumpMemory() {
      java.lang.reflect.Method meth = null;
      Class cls = null;
      
      try {
         Class.forName("com.ibm.jinsight.tracing.JinsightCtrl");
      } catch(ClassNotFoundException e) {}
      
      if (cls != null) {
         ;
      }
      System.out.println("dumpMemory not yet implemented");
   }
}
