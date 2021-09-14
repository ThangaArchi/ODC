package oem.edge.ed.odc.dsmp.common;

import java.util.zip.*;
import java.util.*;

public class Compression {

   protected Deflater deflater = null;
   protected Inflater inflater = null;
   protected int      startv   = 0;
   
   public synchronized int  getStartDeCompressionSize()      { return startv; }
   public synchronized void setStartDeCompressionSize(int s) { startv = s;    }
   
   protected boolean dodebug = false;
   
   public synchronized void setDebug(boolean v) {
      dodebug = v;
   }
   
   public synchronized CompressInfo compress(byte arr[]) {
      return compress(arr, 0, arr.length);
   }
   
   public synchronized CompressInfo compress(byte arr[], int ofs, int count) {
                                   
      if (deflater == null) {
         synchronized(this) {
            if (deflater == null) {
               deflater = new Deflater();
            }
         }
      }
      
      synchronized (deflater) {
      
         deflater.reset();
         deflater.setInput(arr, ofs, count);
         deflater.finish();
         int startsz = count > 50000?200000:count*3;
         byte comparr[] = new byte[startsz];
         int lcount = deflater.deflate(comparr);
         if ((lcount+(lcount>>3)) < count) {
            arr = comparr;
            ofs = 0;
            if (dodebug) {
               System.out.println("Compress: " + count + " -> " + lcount +
                                  " or %" + ((count*100)/lcount));
            }
            count = lcount;
         } else {
            if (dodebug) {
               System.out.println("NO Compress: " + count + " -> " + lcount +
                                  " or %" + ((count*100)/lcount));
            }
         }
      }
      return new CompressInfo(arr, ofs, count);
   }
   
   public synchronized CompressInfo decompress(byte arr[]) throws 
                                           java.util.zip.DataFormatException {
      return decompress(arr, 0, arr.length);
   }
   
   public synchronized CompressInfo decompress(byte arr[], int ofs, 
                                               int len) throws 
                                           java.util.zip.DataFormatException {
      
      if (inflater == null) {
         synchronized(this) {
            if (inflater == null) {
               inflater = new Inflater();
            }
         }
      }
      
      synchronized(inflater) {
         
         int num = len;
      
         inflater.reset();
         inflater.setInput(arr, ofs, len);
      
         num = 0;
         
         int sz = startv;
         if (sz == 0) {
            sz = (len+1)*5;
            if (sz > 200000) sz = 200000;
            if (sz < 16364)  sz = 20000;
         }
         byte debuf[] = new byte[sz];
         while(!inflater.needsInput()) {
            if (num == debuf.length) {
               if (dodebug) {
                  System.out.println(
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
            if (dodebug) {
               System.out.println("Decompress of " + len +
                                  " bytes yielded " + num + " bytes");
            }
            arr = debuf;
            len = num;
            ofs = 0;
         } else {
            System.out.println("Decompress of " + len +
                               " bytes yielded " + num + " bytes!");
         }
      }
      return new CompressInfo(arr, ofs, len);
   }
   
}
