package oem.edge.ed.odc.ftp.common;

import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.tunnel.common.DebugPrint;

import java.io.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2002-2004                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rigts                                 */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */
abstract public class ReceiveFileOperation extends Operation {
   protected OutputStream os = null;
   
  // private byte buf[]      = new byte[50000];
   
   protected IOException ioexception = null;
   
   public ReceiveFileOperation(DSMPBaseHandler handler, int id,
                               long totToXfer, long ofs, File f) {
      super(handler, id, totToXfer, ofs);
      status = STATUS_INPROGRESS;
      try {
         this.os = new FileOutputStream(f.getCanonicalPath(), 
                                        f.length() == ofs);
      } catch (IOException io) {
         this.os = null;
         ioexception = io;
      }
   }
   
  // If this Constructor is used, assumes that OFS is already been skipped
   public ReceiveFileOperation(DSMPBaseHandler handler, int id,
                               long totToXfer, long ofs, OutputStream os) {
      super(handler, id, totToXfer, ofs);
      this.os = os;
      status = STATUS_INPROGRESS;
   }
   
  // Reason should be null unless it describes an error condition
   public synchronized boolean endOperation(String reason) {
      boolean ret = false;
      if (status < STATUS_TERMINATED) {
         
         if (totXfered != totToXfer) {
            if (reason == null) {
               reason= "Total amount xfered != expected\n" +
                  "  " + totXfered + " -> " + 
                  totToXfer; 
            }
         }
         
         try {
            os.flush();
            os.close();
            os = null;
         } catch(Exception e) {
         }
         
         ret = super.endOperation(reason);
      }
      
      return ret;
   }
   
   public void frameData(long ofs, byte buf[], int bofs, int blen) {
      if (status == STATUS_INPROGRESS) {
         if (ioexception != null) {
            endOperation("IOException occured: " + ioexception.getMessage());
            ioexception = null;
            return;
         }
         try {
            os.write(buf, bofs, blen);
            totXfered += blen;
            
            dataTransferred();  // Let someone know
            
            if (totXfered >= totToXfer) {
               endOperation(null);
            }
         } catch(IOException io) {
            DebugPrint.printlnd(DebugPrint.ERROR, 
                                "IOException while receiving/wrinting data:");
            DebugPrint.println(DebugPrint.ERROR, io);
            endOperation("IOException occured: " + io.getMessage());
         } catch(NullPointerException np) {
            endOperation("NullPointerException: Probably an OFS error");
         }
      }
   }
}
