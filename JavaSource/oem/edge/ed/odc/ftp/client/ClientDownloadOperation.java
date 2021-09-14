package oem.edge.ed.odc.ftp.client;

import java.io.File;
import java.io.OutputStream;

import oem.edge.ed.odc.dsmp.common.DSMPBaseHandler;
import oem.edge.ed.odc.dsmp.common.DSMPBaseProto;
import oem.edge.ed.odc.ftp.common.FTPGenerator;
import oem.edge.ed.odc.ftp.common.ReceiveFileOperation;
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
public class ClientDownloadOperation extends ReceiveFileOperation {
   
   public ClientDownloadOperation(DSMPBaseHandler handler, int id,
                                  long totToXfer, long ofs, OutputStream oos) {
      super(handler, id, totToXfer, ofs, oos);
   }
   public ClientDownloadOperation(DSMPBaseHandler handler, int id,
                                  long totToXfer, long ofs, File f) {
      super(handler, id, totToXfer, ofs, f);
   }
   
   public synchronized void  handleEndError(String reason) {
      if (reason != null) {
         DSMPBaseProto proto = null;
         proto=FTPGenerator.abortDownload(handleToUse, id);
         
        // Send and ignore reply
         try {
            handler.getDispatch().sendIgnoreReply(handler, proto);
         } catch(Exception ee) {
            System.out.println("Error submitting abortDownload" + 
                               ee.toString());
         }
         
      } else {
         DSMPBaseProto proto = null;
         proto=FTPGenerator.operationComplete(handleToUse, id);
         handler.sendProtocolPacket(proto);
      }
   }
}
