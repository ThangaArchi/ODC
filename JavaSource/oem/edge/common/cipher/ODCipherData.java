package oem.edge.common.cipher;

import java.math.BigInteger;
import java.lang.*;

/*
** Module : ODCipherData
**
** Purpose: Simple class to return cipher data (no reference pointers in Java!)
**
** Author : Joe Crichton
**
** Change Log:
**
**          05-05-01 - Initial coding
*/
public class ODCipherData {
   byte[] arr;
   int secs;
   public ODCipherData() {
      arr = null;
      secs = 0;
   }
   public ODCipherData(byte inarr[], int ofs, int len, int insecs) {
      setValues(inarr, ofs, len, insecs);
   }
   
   public void setValues(byte inarr[], int ofs, int len, int insecs) {
      arr = new byte[len];
      System.arraycopy(inarr, ofs, arr, 0, len);
      secs = insecs;
   }
   
   public boolean isCurrent()         { 
      return (System.currentTimeMillis()/1000) < secs;
   }

   public byte[]  getBytes()          { return arr;  }
   public int     getSecondsSince70() { return secs; }
   public String  getString()         { return new String(arr); }   
   public String  getExportString()   { 
      int tlen = arr.length;
      StringBuffer sb = new StringBuffer(tlen*2);
      for(int i=0; i < tlen; i++) {
         sb.append(Character.forDigit(((arr[i] >> 4) & 0xf), 16));
         sb.append(Character.forDigit(((arr[i])      & 0xf), 16));
      }
      return sb.toString();
   }   
}
