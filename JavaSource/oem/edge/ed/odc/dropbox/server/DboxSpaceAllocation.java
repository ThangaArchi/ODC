package oem.edge.ed.odc.dropbox.server;

public class DboxSpaceAllocation extends SpaceAllocation {
   DboxFileArea filearea = null;
   
   public DboxSpaceAllocation(DboxFileArea fa, long s) {
      super(fa.getTopLevelDirectory(), s);
      filearea = fa;
   }
      
   public DboxFileArea getFileArea() { return filearea; }
}
