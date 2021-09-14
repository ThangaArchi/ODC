package oem.edge.ed.odc.ftp.client;

import java.io.File;
import java.io.InputStream;

import oem.edge.ed.odc.dsmp.common.DSMPBaseHandler;
import oem.edge.ed.odc.dsmp.common.DSMPBaseProto;
import oem.edge.ed.odc.dsmp.common.ProtoSentListener;
import oem.edge.ed.odc.ftp.common.FTPGenerator;
import oem.edge.ed.odc.ftp.common.SendFileOperation;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2006                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rights                                */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */
public class ClientUploadOperation extends    SendFileOperation 
                                   implements ProtoSentListener{
      
   protected int numout           = 0;
   protected long totalconfirmed  = 0;
   static final int MAX_PROTO_OUT = 6;
   
   public ClientUploadOperation(DSMPBaseHandler handler, int id, 
                                long totToXfer, long ofs, InputStream iis) {
      super(handler, id, totToXfer, ofs, iis);
   }
   public ClientUploadOperation(DSMPBaseHandler handler, int id, 
                                long totToXfer, long ofs, File f) {
      super(handler, id, totToXfer, ofs, f);
   }
   
   
   public long getTotalConfirmed() { return totalconfirmed; }
   
   public int getNumOutstandingProtoPackets() { return numout; }
   
   public synchronized void  handleEndError(String reason) {
      if (reason != null) {
         DSMPBaseProto proto = null;
         proto=FTPGenerator.abortUpload(handleToUse, id);
         
        // Handle this as low since we did the data packets as well
         proto.setLowPriority(true);
         
        // Send and ignore reply
         try {
            handler.getDispatch().sendIgnoreReply(handler, proto);
         } catch(Exception ee) {
            System.out.println("Error submitting abortUpload" + ee.toString());
         }
         
         System.out.println("Abort upload because " + reason);
      }
   }
   
   public synchronized void fireSentEvent(DSMPBaseProto p) {
      
      totalconfirmed += p.getNonHeaderSize() - 12;
      dataTransferred();
      
      if (--numout < 0) {
         System.out.println("Zoinks! numout in fireSentEvent < 0!!");
         numout = 0;
      }
      if (handler.getDispatch().getMinorDebug()) {
         System.out.println("fireSentEvent: Out=" + numout + " time[" + System.currentTimeMillis() + "]");
      }
      notifyAll();
   }

   public void sendData(long tofs, byte arr[], int bofs, int blen) {
      DSMPBaseProto proto = null;
      proto=FTPGenerator.uploadData(handleToUse, id, tofs, arr, bofs, blen);
      proto.addSentListener(this);
      
     // If this is the first packet, just return, don't block, otherwise,
     //  We want to keep from saturating the upload stream, and we get 
     //  callbacks
      synchronized(this) {
         while(numout >= MAX_PROTO_OUT) {
            try {
               if (handler.getDispatch().getMinorDebug()) {
                  System.out.println("sendData: BLOCK ProtoOut=" + numout + " time[" + System.currentTimeMillis() + "]");
               }
               wait(10000);
            } catch(InterruptedException ie) {}
         }
         numout++;
         
         if (handler.getDispatch().getMinorDebug()) {
            System.out.println("sendData: Sending Proto Out=" + numout + " time[" + System.currentTimeMillis() + "] : " + (proto.memoryFootprint()/1024) + "K");
         }
         
        // This packet can be trumped by other less volumnous items
         proto.setLowPriority(true);
         
         handler.sendProtocolPacket(proto);
         if (handler.getDispatch().getMinorDebug()) {
            System.out.println("sendData: Sending Proto Out2=" + numout + " time[" + System.currentTimeMillis() + "] : " + (proto.memoryFootprint()/1024) + "K");
         }
      }
   }
}
