package oem.edge.ed.odc.tunnel.common;
public interface Sender {
  // Sender can be asked to bag out (outside of the implied shutdown via SM)
   void bagout();
}
