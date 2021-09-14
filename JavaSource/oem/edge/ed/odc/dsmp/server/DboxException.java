package oem.edge.ed.odc.dsmp.server;

import java.lang.*;

public class DboxException extends Exception {

   public static final int GENERAL_ERROR = 0;
   public static final int SQL_ERROR     = 99;

   int majorCode = 0;
   int minorCode = 0;
   Throwable subthrowable;
   
   public DboxException(String msg) {
      super(msg);
   }
   public DboxException(String msg, Throwable t) {
      super(msg);
      subthrowable = t;
   }
   public DboxException(String msg, int code) {
      super(msg);
      majorCode = code;
   }
   public DboxException(String msg, int majcode, int mincode) {
      super(msg);
      majorCode = majcode;
      minorCode = mincode;
   }
      
   public int getErrorCode() { return getMajorCode(); }
   public int getMajorCode() { return majorCode;      }
   public int getMinorCode() { return minorCode;      }
   
   public String toString() {
      String ret=super.toString();
      
      if (subthrowable != null) {
         ret = "\nNested Exception:\n\n" + subthrowable.toString();
      }
      return ret;
   }
}
