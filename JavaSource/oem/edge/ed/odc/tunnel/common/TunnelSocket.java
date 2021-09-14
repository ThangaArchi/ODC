package oem.edge.ed.odc.tunnel.common;

import java.io.*;
import java.net.*;

public class TunnelSocket implements TunnelCommon {
   private Socket socket;
   private SocketInputBuffer inputBuffer;
   private SocketOutputBuffer outputBuffer;
   private byte myid;
   private String earType  = null;
   private byte earid;/* subu 05/20/02 */ 
   private boolean callMainConnComplete = false;
   private SessionManager sessionMgr;
   
   public TunnelSocket(SessionManager ss, String et, Socket socket, 
                       byte earid, byte id, String xcook) throws IOException {
					   
      this.socket = socket;
      this.earid=earid;/* subu 05/20/02 */
      earType = et;
      sessionMgr = ss;
      myid = id;
      if (socket != null) {
         inputBuffer = 
            new SocketInputBuffer(sessionMgr, 
                            new BufferedInputStream(socket.getInputStream()), 
                                  earid,
                                  id);
      } else {                               
         inputBuffer = new SocketInputBuffer(sessionMgr, null, earid, id);
      }
      
     // JMC 3/20/02 - Needed for compression
      inputBuffer.isX(isX());
      inputBuffer.isICA(isICA());
      
      if (socket != null) {
         outputBuffer 
            = new SocketOutputBuffer(sessionMgr, socket.getOutputStream(), 
                                     isX()?xcook:null);
      } else {
         outputBuffer = new SocketOutputBuffer(sessionMgr, null, 
                                               isX()?xcook:null);
      }
   }   
   public void setCallMainConnComplete(boolean v) {
      callMainConnComplete = v;
   }
   
   public byte getEarId() {return earid;}/* subu 05/20/02 */
   public byte getId() {
      return myid;
   }   
   public SocketInputBuffer getInputBuffer() {
      return inputBuffer;
   }   
   public SocketOutputBuffer getOutputBuffer() {
      return outputBuffer;
   }   
   public boolean isICA() {
      return earType.equals("ica");
   }   
   public boolean isX() {
      return earType.equals("X");
   }   
   public boolean isIM() {
      return earType.equals("IM") || earType.equals("IMS");
   }   
   public boolean isRM() {
      return earType.equals("RM") || earType.equals("STM");
   }   
   public void shutdown() {
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG,
                            "TunnSock: SHUTDOWN enter: " + toString());
      }
      
      try {
         inputBuffer.shutdown();
      } catch(Exception e) {}
      try {
         outputBuffer.shutdown();
      } catch(Exception e) {}
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG,
                            "TunnSock: SHUTDOWN exit: ");
      }
      
      if (sessionMgr != null && callMainConnComplete) {
         sessionMgr.mainConnectionComplete();
      }
      
   }   
   public String toString() {
      return "TunnelSocket: InstId=" + myid 
         + "\n  InpBuf=" + inputBuffer.toString()
         + "\n  OutBuf=" + outputBuffer.toString() 
         + "\n";
   }   
}
