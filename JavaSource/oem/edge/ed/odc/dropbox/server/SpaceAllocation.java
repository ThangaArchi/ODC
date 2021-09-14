package oem.edge.ed.odc.dropbox.server;

public class SpaceAllocation {
   long sz = 0;
   String dir = null;
   
   public SpaceAllocation(String d, long s) {
      sz = s; dir = d;
   }
   
   public String getDirectory() { return dir; }
   public long   getSize()      { return sz;  }
}
