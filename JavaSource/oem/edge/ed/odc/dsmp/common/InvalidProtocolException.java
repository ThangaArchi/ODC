package oem.edge.ed.odc.dsmp.common;

import java.lang.Exception;
public class InvalidProtocolException extends Exception {
   public InvalidProtocolException(String s) {
      super(s);
   }
   public InvalidProtocolException() {
      super("InvalidProtocolException");
   }
}
