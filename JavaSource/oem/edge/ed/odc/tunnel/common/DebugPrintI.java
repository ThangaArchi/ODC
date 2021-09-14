package oem.edge.ed.odc.tunnel.common;
public interface DebugPrintI {

   public void setInAnApplet(boolean d);
   public boolean inAnApplet();
   public void setClientSide(boolean d);
   public boolean getClientSide();
   public void refresh();
   public boolean doDebug();
   public int getLevel();
   public void setLevel(int d);
   public void println(int lev, String s);
   public void println(String s);
   public void println(int lev, Throwable e);
   public void printlnd(int lev, String s);
   public void printlnd(String s);
   public void printlnd(int lev, Throwable e);
}
