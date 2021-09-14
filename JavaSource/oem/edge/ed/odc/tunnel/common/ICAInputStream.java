package oem.edge.ed.odc.tunnel.common;

/**
**  Used to interface JICA to Tunnel. ToDo: Not currently supporting Timeouts,
**   and since its used in the Socket env, we should.
*/

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 1998,2004,2005,2006		                 */ 
/*                                                                       */ 
/*     All Rights Reserved					         */ 
/*     US Government Users Restricted Rights			         */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 

import oem.edge.ed.odc.tunnel.common.*;
public class ICAInputStream extends java.io.ByteArrayInputStream {
	
   protected ICAOutputStream out = null;
   protected boolean isclosed = false;
   
   protected int MAX_BUFFERED = 1024*100;
   
  // Total data pushed thru inputstream
   protected long totdata = 0;
   
   public long getTotalQueued() { return totdata; }
   
   public void setMaxBuffered(int size) {
      MAX_BUFFERED = size;
   }
   public int getMaxBuffered() {
      return MAX_BUFFERED;
   }
   
   protected String name; 
   public void setName(String n) {
      name = n;
   }
   
   public ICAInputStream() {
      super(new byte[4096]);
      pos = count = 0;
   }
   public ICAInputStream(byte[] buf) {
      super(buf);
      pos = count = 0;
   }
   public ICAInputStream(byte[] buf, int offset, int length) {
      super(buf, offset, length);
      pos = count = 0;
   }
   
   public synchronized int capacity(int l) {
      if (buf.length < l) {
         byte newb[] = new byte[l];
         if (count > pos) {
            System.arraycopy(buf, pos, newb, 0, count-pos);
            count -= pos;
         } else {
            count = 0;
         }
         buf    = newb;
         pos    = 0;
      }
      return buf.length;
   }
   
   public synchronized ICAOutputStream getOutputStream() {
      if (out == null) out = new ICAOutputStream(this);
      return out;
   }
   
   public synchronized void close() throws java.io.IOException {
     //if (isclosed) throw new java.io.IOException("Already closed!");
      isclosed = true;
      this.notifyAll();
     //super.close();
   }
   
   public synchronized boolean isClosed() {
      return isclosed;
   }
   
   private synchronized void leftAdjust() {
      if (pos > 0) {
         if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
            DebugPrint.println(DebugPrint.DEBUG, name+": ADJUSTING!");
         }
         if (count > pos) {
            System.arraycopy(buf, pos, buf, 0, count-pos);	
            count -= pos;
         } else {
            count = 0;
         }
         pos = 0;
      }
   }
   
   private synchronized void leftAdjustIfCanFit(int len) {
      if (pos > 0 && buf.length >= (len + available()) && 
          (buf.length - count) < len) {
         leftAdjust();
      }
   }
   
   public boolean markSupported() {
      return false;
   }
   
   public synchronized int read() {
         
      if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
         DebugPrint.println(DebugPrint.DEBUG, name + 
                            ": ICAIS READ char top");
      }
      
      waitForData();
      
      int ret = super.read();
      
      this.notifyAll();
      if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
         DebugPrint.println(DebugPrint.DEBUG, name + 
                            ": ICAIS READ char DONE");
      }
      return ret;
   }
   
   public synchronized int read(byte b[]) {
      return read(b, 0, b.length);
   }
   
   public synchronized int read(byte b[], int off, int len) {
      
      if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
         DebugPrint.println(DebugPrint.DEBUG, name + 
                            ": ICAIS READ arr top: " + len);
      }
                         
      if (len > 1000000) {
         DebugPrint.println(DebugPrint.INFO5,"ICAInputStream: reading len = " + len);
         DebugPrint.println(DebugPrint.INFO5, (new java.io.IOException()));
      }
      waitForData();
      int ret = super.read(b, off, len);
      
//      DebugPrint.println("Doing PB:\n" + DebugPrint.showbytes(b, off, ret));
      
      if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
         DebugPrint.println(DebugPrint.DEBUG, name + 
                            ": Calling NOTIFYALL: " + toString());
      }
      
      this.notifyAll();
      if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
         DebugPrint.println(DebugPrint.DEBUG, name + 
                            ": NOTIFYALL Done");
      }
      if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
         DebugPrint.println(DebugPrint.DEBUG, name + 
                            ": ICAIS READ arr DONE: " + ret);
      }
      return ret;
   }
   
   public void reset() {
      if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
         DebugPrint.println(DebugPrint.DEBUG, name+
                            ": -------------- CALLING RESET ---------------!");
      }
   }
   
   public long skip(long n) {
      if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
         DebugPrint.println(DebugPrint.DEBUG, name+
                            ": -------------- CALLING SKIP ---------------!");
      }
      int av = available();
      if (n <= av) {
         return super.skip(n);
      }
      byte b[] = new byte[(int)n>4096?4096:(int)n];
      long tot = 0;
      while(!isclosed && tot < n) {
         int toread = (int)(n-tot);
         if (toread > b.length) toread = b.length;
         waitForData();
         int r = read(b, 0, toread);
         if (r < 0) break;
         tot += r;
      }
      notifyAll();
      return tot;
   }
   
   public synchronized void waitForData() {
      if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
         DebugPrint.println(DebugPrint.DEBUG, name+
                            ": ICAInputStream waitForData TOP");
      }
      while(!isclosed && available() < 1) {
         try {
            if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
               DebugPrint.println(DebugPrint.DEBUG, name+
                                  ": ICAIS waitForData calling wait");
            }
            wait();
         } catch(InterruptedException e) {
         }
      }
      if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
         DebugPrint.println(DebugPrint.DEBUG, name +
                            ": ICAInputStream waitForData ret len = " + 
                            available());
      }
   }
   
   public synchronized void write(byte b[], int off, int len)
                                                throws java.io.IOException {
   
      if (isclosed) throw new java.io.IOException("closed!");
      if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
         DebugPrint.println(DebugPrint.DEBUG, name + 
                            ": ICAInputStream write " + len);
      }
      
      if (len < 0 || len > b.length-off) {
         throw new java.io.IOException("Bad length/off");
      }
      
      while(!isclosed && len > 0) {
         int canfit = MAX_BUFFERED - available();
         
         while(!isclosed && canfit <= 0) {
            if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
               DebugPrint.println(DebugPrint.DEBUG, name + 
                                  ": ICAIS write: Blocking. Writing " + len +
                                  " exceeds MAX (" + MAX_BUFFERED + "): " + 
                                  (available()+len));
            }
            try {
               if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
                  DebugPrint.println(DebugPrint.DEBUG, name+
                                     ": ICAIS write calling wait: " + 
                                     toString());
               }
               this.wait();
            } catch(InterruptedException e) {
            }
            
            if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
               DebugPrint.println(DebugPrint.DEBUG, name+
                                  ": ICAIS write back from wait");
            }
            canfit = MAX_BUFFERED - available();
         }
         
         if (isclosed) {
            throw new java.io.IOException("ICAInpStream closed!");
         }
         
         if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
            DebugPrint.println(DebugPrint.DEBUG, name + 
                               ": ICAIS: canfit = " + canfit +
                               " len = " + len);
         }
         
         if (len < canfit) canfit = len;
         
         leftAdjustIfCanFit(canfit);
         capacity(available() + canfit);
         
         if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
            DebugPrint.println(DebugPrint.DEBUG, name + 
                               ": ICAIS: arrcopy blen = " + b.length +
                               " off = " + off + 
                               " buflen = " + buf.length + 
                               " count(ofs) = " + count + 
                               " pos = " + pos + 
                               " avail = " + available() + 
                               " canfit(len) = " + canfit);
                               
                               
         }
         System.arraycopy(b, off, buf, count, canfit);
         
         count += canfit;
         len -= canfit;
         off += canfit;
         
         totdata += canfit;
      
         this.notifyAll();
      }
      
      if (isclosed) throw new java.io.IOException("closed!");
      
      if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
         DebugPrint.println(DebugPrint.DEBUG, name + 
                            ": ICAInputStream write Done");
      }
   }
}
