package oem.edge.ed.odc.dropbox.server;

import java.lang.*;
import java.io.*;
import oem.edge.ed.odc.tunnel.common.DebugPrint;

// Now just a thin shell
class Debug {
   static protected boolean getDebug()  { return DebugPrint.doDebug(); }
   static protected void setDebug(boolean v) { 
      if (v) {
         if (DebugPrint.getLevel() < DebugPrint.DEBUG) {
            DebugPrint.setLevel(DebugPrint.DEBUG);
         }
      } else if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
         DebugPrint.setLevel(DebugPrint.INFO5);
      }
   }
   static protected void debugprint(String s) {
      DebugPrint.printlnd(DebugPrint.DEBUG, s);
   }
}
