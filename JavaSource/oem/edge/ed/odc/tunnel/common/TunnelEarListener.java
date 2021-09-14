package oem.edge.ed.odc.tunnel.common;
public interface TunnelEarListener {
   public void socketCreated(TunnelSocket ts);
   public void socketDestroyed(TunnelSocket ts);
   public void earDestroyed(TunnelEarInfo te);
}
