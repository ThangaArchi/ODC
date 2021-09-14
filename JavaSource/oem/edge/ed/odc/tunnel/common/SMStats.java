package oem.edge.ed.odc.tunnel.common;
import java.util.Date;

public class SMStats {
   protected long elap;
   protected Date lastResetV;
   protected long totin;
   protected long totout;
   protected long totUcompIn;
   protected long totUcompOut;
   protected long totCin;
   protected long totCout;
   protected long totlocalin;
   protected long totlocalout;
   
   public static final int MAX_PINGS = 10;
   protected long   totpingms;
   protected long   totpings;
   protected long   maxping;
   protected long   minping;
   protected int    numping;
   protected int    pingid[] = new int [MAX_PINGS];
   protected long   pingms[] = new long[MAX_PINGS];
   protected long   pingbi[] = new long[MAX_PINGS];
   protected long   pingbo[] = new long[MAX_PINGS];
   protected long   pingtm[] = new long[MAX_PINGS];
   
   public SMStats() {
      reset();
   }
   
   public SMStats(SMStats in) {
      elap        = in.elap;
      totin       = in.totin;
      totout      = in.totout;
      totCin      = in.totCin;
      totCout     = in.totCout;
      totUcompIn  = in.totUcompIn;
      totUcompOut = in.totUcompOut;
      totlocalin  = in.totlocalin;
      totlocalout = in.totlocalout;
      
      numping     = in.numping;
      totpingms   = in.totpingms;
      totpings    = in.totpings;
      maxping     = in.maxping;
      minping     = in.minping;
      
      System.arraycopy(in.pingid, 0, pingid, 0, MAX_PINGS);
      System.arraycopy(in.pingms, 0, pingms, 0, MAX_PINGS);
      System.arraycopy(in.pingtm, 0, pingtm, 0, MAX_PINGS);
      System.arraycopy(in.pingbi, 0, pingbi, 0, MAX_PINGS);
      System.arraycopy(in.pingbo, 0, pingbo, 0, MAX_PINGS);
      
      lastResetV  = new Date(in.lastResetV.getTime());
   }
   
   public synchronized void addPingInfo(int id, long ms, 
                                        long datain, long dataout) {
          
      if (ms < minping) minping = ms;
      if (ms > maxping) maxping = ms;
      totpingms += ms;
      totpings++;
      
      if (numping == MAX_PINGS) {
         System.arraycopy(pingid, 1, pingid, 0, MAX_PINGS-1);
         System.arraycopy(pingms, 1, pingms, 0, MAX_PINGS-1);
         System.arraycopy(pingbi, 1, pingbi, 0, MAX_PINGS-1);
         System.arraycopy(pingbo, 1, pingbo, 0, MAX_PINGS-1);
         System.arraycopy(pingtm, 1, pingtm, 0, MAX_PINGS-1);
         numping--;
      }
      pingid[numping] = id;
      pingms[numping] = ms;
      pingbi[numping] = datain;
      pingbo[numping] = dataout;
      pingtm[numping] = System.currentTimeMillis();
      numping++;
   }
   
   public synchronized void reset() {
      elap = -1;
      totin = 0;
      totout = 0;
      totCin = 0;
      totCout = 0;
      totUcompIn = 0;
      totUcompOut = 0;
      totlocalin = 0;
      totlocalout = 0;
      lastResetV = new Date();
      numping   = 0;
      totpings  = 0;
      totpingms = 0;
      maxping   = 0;
      minping   = 99999999;
   }
   
   public synchronized void addMerge(SMStats in) {
      totin       += in.totin;
      totout      += in.totout;
      totCin      += in.totCin;
      totCout     += in.totCout;
      totlocalin  += in.totlocalin;
      totUcompIn  += in.totUcompIn;
      totUcompOut += in.totUcompOut;
      totlocalout += in.totlocalout;
      
      totpings   += in.totpings;
      totpingms  += in.totpingms;
      if (in.maxping > maxping) maxping = in.maxping;
      if (in.minping < minping) minping = in.minping;
      
      if (lastResetV.after(in.lastResetV)) {
         lastResetV  = new Date(in.lastResetV.getTime());
         
         numping     = in.numping;
         System.arraycopy(in.pingid, 0, pingid, 0, MAX_PINGS);
         System.arraycopy(in.pingms, 0, pingms, 0, MAX_PINGS);
         System.arraycopy(in.pingbi, 0, pingbi, 0, MAX_PINGS);
         System.arraycopy(in.pingbo, 0, pingbo, 0, MAX_PINGS);
         System.arraycopy(in.pingtm, 0, pingtm, 0, MAX_PINGS);
      }
      
      if (in.elap > elap) elap = in.elap;
   }
   
   public int  getNumPings()            { return numping;               }
   public long getPingId(int i)         { return pingid[i];             }
   public long getPingMS(int i)         { return pingms[i];             }
   public long getPingTime(int i)       { return pingtm[i];             }
   public long getPingBytesIn(int i)    { return pingbi[i];             }
   public long getPingBytesOut(int i)   { return pingbo[i];             }
   
   public long getPingAvgMS()  { return totpings>0?totpingms/totpings:0;}
   public long getPingMinMS()  { return minping;                        }
   public long getPingMaxMS()  { return maxping;                        }
   
   
   public long getTotIn()               { return totin       + totCin;  }
   public long getTotOut()              { return totout      + totCout; }
   public long getTotUncompressedIn()   { return totUcompIn  + totCin;  }
   public long getTotUncompressedOut()  { return totUcompOut + totCout; }
   public long getDataIn()              { return totin;                 }
   public long getDataOut()             { return totout;                }
   public long getUncompressedDataIn()  { return totUcompIn;            }
   public long getUncompressedDataOut() { return totUcompOut;           }
   public long getTotControlIn()        { return totCin;                }
   public long getTotControlOut()       { return totCout;               }
   public Date getResetDate()           { return lastResetV;            }
   
   public synchronized void incrLocalIn(int amt) {
      totlocalin += amt;
   }      
   
   public synchronized void incrLocalOut(int amt) {
      totlocalout += amt;
   }      
   
   public synchronized void incrIn(int amt) {
      totin += amt;
      totUcompIn += amt;
   }
   public synchronized void incrOut(int amt) {
      totout += amt;
      totUcompOut += amt;
   }         
   public synchronized void incrIn(int amt, int uncomp) {
      if (uncomp <= 0) {
         uncomp = amt;
      }
      totin += amt;
      totUcompIn += uncomp;
   }      
   public synchronized void incrOut(int amt, int uncomp) {
      if (uncomp <= 0) {
         uncomp = amt;
      }
      totout += amt;
      totUcompOut += uncomp;
   }         
   public synchronized void incrControlIn(int amt) {
      totCin += amt;
   }      

   public synchronized void incrControlOut(int amt) {
      totCout += amt;
   }
   
   
   public static String getHTMLTableHeader() {
      return 
         "<th>Elapsed</th><th>TotBytes</th><th>bps</th>" +
         "<th>UncompTot</th><th>Comp%</th>" +
         "<th>TotIn</th><th>bps</th><th>TotOut</th><th>bps</th>" +
         "<th>DataIn</th><th>bps</th><th>DataOut</th><th>bps</th>" +
         "<th>LocalIn</th><th>bps</th><th>LocalOut</th><th>bps</th>" +
         "<th>OverIn</th><th>OverOut</th><th>TotOver</th>" +
         "<th>AvgPing</th><th>MaxPing</th><th>MinPing</th>";
   }
   
   static public String msToTime(long t) {
      t /= 1000;
      
      if (t <= 0) t = 0;
      
      String elapS = "";
      long days, hours, mins;
      days = t/86400;
      if (days > 0) {
         elapS = "" + days + " days, ";
         t -= days*86400; 
      }
      
      hours = t / 3600;
      t    -= hours*3600;
      
      mins  = t / 60;
      t    -= mins*60;
      
      if (hours < 9) elapS = elapS + "0";
      elapS = elapS + hours + ":";
      
      if (mins < 9) elapS = elapS + "0";
      elapS = elapS + mins + ":";
      
      if (t < 9) elapS = elapS + "0";
      elapS = elapS + t;
      
      return elapS;
   }
   
   public String toHTMLString(Date endd) {
   
      long d = endd.getTime();
      d -= lastResetV.getTime();
      
      if (elap >= 0) d = elap;
      
      String elapS = msToTime(d);
      
      d /= 1000;
      
      if (d <= 0) d = 1;
      
      long tot  = getTotIn()+getTotOut();
      long utot = getTotUncompressedIn()+getTotUncompressedOut();
      long percnt = utot > 0?(100-(tot*100)/utot):0;
      
      String ret = 
         "<td>" + elapS                        + "</td>" +
         "<td>" + (tot)            + "</td>" +
         "<td>" + (tot/d)          + "</td>" +
         
         "<td>" + (utot)           + "</td>" +
         "<td>" + percnt           + "</td>" +
               
         "<td>" + getTotIn()       + "</td>" +
         "<td>" + (getTotIn()/d)   + "</td>" +
         "<td>" + getTotOut()      + "</td>" +
         "<td>" + (getTotOut()/d)  + "</td>" +
         "<td>" + totin            + "</td>" +
         "<td>" + (totin/d)        + "</td>" +
         "<td>" + totout           + "</td>" +
         "<td>" + (totout/d)       + "</td>" +
         "<td>" + totlocalin       + "</td>" +
         "<td>" + (totlocalin/d)   + "</td>" +
         "<td>" + totlocalout      + "</td>" +
         "<td>" + (totlocalout/d)  + "</td>" +
         "<td>" + totCin           + "</td>" +
         "<td>" + totCout          + "</td>" +
         "<td>" + (totCout+totCin) + "</td>" +
         "<td>" + getPingAvgMS()   + "</td>" +
         "<td>" + maxping          + "</td>" +
         "<td>" + minping          + "</td>";
      return ret;
   }
   
   public void freezeElap(Date indate) {
      elap = indate.getTime();
      elap -= lastResetV.getTime();
      if (elap <= 0) elap = 1;
   }
   
   public void freezeElap(long msSince70) {
      elap = msSince70;
   }
   
   public String toHTMLString() {
      return toHTMLString(new Date());
   }
   
}
