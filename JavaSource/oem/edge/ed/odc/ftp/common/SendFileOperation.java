package oem.edge.ed.odc.ftp.common;

import  oem.edge.ed.odc.dsmp.common.*;
import oem.edge.ed.odc.tunnel.common.DebugPrint;

import java.io.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2002-2004,2005,2006                           */
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
public abstract class SendFileOperation extends Operation implements Runnable {
   protected InputStream is   = null;
   protected File        file = null;
   private   byte buf[]       = null;

   public SendFileOperation(DSMPBaseHandler handler, int id,
                            long totToXfer, long ofs, InputStream is) {
      super(handler, id, totToXfer, ofs);
      this.is = is;      
   }
   
   public SendFileOperation(DSMPBaseHandler handler, int id,
                            long totToXfer, long ofs, File f) {
      super(handler, id, totToXfer, ofs);
      file = new File(f.getPath());
   }
   
  // Reason should be null unless it describes an error condition
   public synchronized boolean endOperation(String reason) {
      boolean ret = false;
      if (status < STATUS_TERMINATED) {
         buf = null;
         if (totXfered != totToXfer) {
            if (reason == null) {
               reason= "Total amount xfered != expected\n" +
                  "  " + totXfered + " -> " + totToXfer; 
            }
         }
         
         try {
            is.close();
            is = null;
         } catch(Exception e) {
         }
         
         ret = super.endOperation(reason);
      }
      
      return ret;
   }
   
  // Override this to actually send the data to the other party
   public abstract void sendData(long tofs, byte arr[], int bofs, int blen);
      
   public void run() {
      setStatus(STATUS_INPROGRESS);
      try {
      
        // If we have a File but no IS, create IS now
         if (file != null && is == null) {
            this.is = new FileInputStream(file);
         }
         
         if (file == null && is == null) {
            throw new IOException("SendFileOperation: No File or stream provided");
         }
      
         if (totToXfer == 0) {
            endOperation(null);
            return;
         }
      
        // Skip ofs amount
         if (ofs > 0) {
            long lofs = ofs;
            while(lofs > 0) {
              //int r = (int)(buf.length > lofs ? lofs : buf.length);
              //r = is.read(buf, 0, r);
               long r = is.skip(lofs);
               if (r < 0) {
                  endOperation(null);
               } else {                  
                  lofs -= r;
               }
            }
         }
      
         int bofs = 0;
         buf = new byte[50000];
         int FLUSHSIZE = buf.length / 2;
         
         while(status == STATUS_INPROGRESS) {
            int r = is.read(buf, bofs, buf.length-bofs);
            
           // If EOF
            if (r < 0) {
            
              // Send any data we have been queuing
               if (bofs > 0) {
                  sendData(totXfered+ofs, buf, 0, bofs);
                  totXfered += bofs;
                  bofs = 0;
               }
               
              // If we sent MORE or LESS than we should have ... just endOp
              //  this will cause an Abort
               if (totXfered != totToXfer) {
                  endOperation(null);
               } else {
                 // Otherwise ... just change our status ... we are now waiting
                 //               for OpComplete from other side
                  synchronized(this) {
                     if (status < STATUS_TERMINATED) {
                        setStatus(STATUS_AWAITING_CONFIRMATION);
                     }
                  }
               }
            } else if ((totXfered+r+bofs) > totToXfer) {
            
              // If we have got TOO much data ... complain

               endOperation("File bigger than expected: " + 
                            (totXfered+r+bofs) + " -> " + totToXfer);
            } else {
            
               bofs += r;
               if (bofs > FLUSHSIZE) {                  
                 // If we have enough to warrent sending ... send it
                  sendData(totXfered+ofs, buf, 0, bofs);
                  totXfered += bofs;
                  bofs = 0;
               }
            }
         }
      } catch(IOException io) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "IOException while reading/sending data:");
         DebugPrint.println(DebugPrint.ERROR, io);
         endOperation("IOException occured: " + io.getMessage());
      } catch(NullPointerException np) {
         endOperation("Probably an OFS error");
      }
   }
}
