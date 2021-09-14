package oem.edge.ed.odc.util;

public class Nester {
   static public String nest(String s) { return nest(s, 1); }
   static public String nest(String s, int lev) {
      if (lev > 10) lev = 10;
      if (lev <= 0) return s;
      
      String rep = "   ";
      while(--lev > 0) rep += "   ";
      
      StringBuffer ret = new StringBuffer();
      
      int idx = 0;
      int nidx;
      while((nidx=s.indexOf('\n', idx)) > -1) {
         ret.append(rep);
         ret.append(s.substring(idx, nidx+1));
         idx = nidx+1;
      }
      ret.append(rep).append(s.substring(idx));
      return ret.toString();
   }
}
