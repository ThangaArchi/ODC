package oem.edge.ed.odc.ftp.client;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import oem.edge.ed.odc.dsmp.common.DSMPBaseHandler;
import oem.edge.ed.odc.dsmp.common.DSMPBaseProto;
import oem.edge.ed.odc.dsmp.common.DSMPSocketHandler;
import oem.edge.ed.odc.ftp.common.FTPDispatchBase;
import oem.edge.ed.odc.ftp.common.FTPGenerator;
import oem.edge.ed.odc.ftp.common.Operation;
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

public class FTPClient extends FTPDispatchBase {

   Hashtable ops = new Hashtable();
   void saveOperation(int id, Operation op) {
      ops.put(new Integer(id), op);
   }
   Operation getOperation(int id) {
      return (Operation)ops.get(new Integer(id));
   }
   

   int localport = -1;
   String user  = null;
   String pw    = null;
   String token = null;
   String servername = null;
   public boolean parseArgs(String args[]) {
      for(int i=0; i < args.length; i++) {
         if (args[i].equalsIgnoreCase("-host")) {
            servername = args[++i];
            try {
               localport = Integer.parseInt(args[++i]);
            } catch(Throwable tt) {
               System.out.println("-port take an integer value");
               return false;
            }
      } else if (args[i].equalsIgnoreCase("-debug")) {
         setDebug(true);
      } else if (args[i].equalsIgnoreCase("-userpw")) {
            try {
               user = args[++i];
               pw   = args[++i];
            } catch (Throwable tt) {
               System.out.println("Error processing -userpw option takes user and pw parms");
               tt.printStackTrace(System.out);
               return false;
            }
         } else if (args[i].equalsIgnoreCase("-token")) {
            try {
               token   = args[++i];
            } catch (Throwable tt) {
               System.out.println("Error processing -token option takes a token parm");
               tt.printStackTrace(System.out);
               return false;
            }
         } else {
            System.out.println(args[i] + ": unknown option\n\nUsage:\n" +
                               "DSMPServer [-host hostname portno] [-userpw user pw] [-token token]");
            return false;
         }
      }
      return true;
   }
   
   public static void main(String args[]) {
      FTPClient client = new FTPClient();
      client.domain(args);
   }
   
   
   public void domain(String args[]) {
   
      byte hand = 1;
      if (!parseArgs(args)) return;
      try {
         DSMPBaseHandler handler = new DSMPSocketHandler(servername, 
                                                         localport, 
                                                         this);
         DSMPBaseProto proto;
         if (user != null) {
            proto = FTPGenerator.loginUserPW(hand++, user, pw);
         } else if (token != null) {
            proto = FTPGenerator.loginToken(hand++, token);
         } else {
            System.out.println("Dude, you have to let me know who you are to log in");
            return;
         }
         
         System.out.println("Sending ProtoPacket");
         handler.sendProtocolPacket(proto);
      } catch(Throwable tt) {
         System.out.println("Error doing the client deed => ");
         tt.printStackTrace(System.out);
      }
      synchronized (this) {
         try {
            wait();
         } catch(InterruptedException e) {}
      }
   }
   
   
  /* -------------------------------------------------------*\
  ** Replies
  \* -------------------------------------------------------*/
  
   public void fireLoginReply(DSMPBaseHandler h, byte flags, byte handle,
                              int  loginid, String area, String sep) {
      h.sendProtocolPacket(FTPGenerator.listArea((byte)0));
   }
   
   public void fireLogoutReply(DSMPBaseHandler h, byte flags, byte handle) {
      System.exit(3);
   }

   public void fireListAreaReply(DSMPBaseHandler h, byte flags, byte handle,
                                 Vector v) {
      
      if (handle == 0) {
         h.sendProtocolPacket(FTPGenerator.changeArea((byte)0, "/tmp/local")); 
      } else {
         File f = new File("testdown");
         long ofs = 0;
         int  crc = 0;
         try {
            crc=calculateCRC(f);
            ofs = f.length();
         } catch(IOException io) {
            System.out.println("Can't restart, no local file");
         }
         h.sendProtocolPacket(FTPGenerator.download((byte)0, true, crc, 
                                                    ofs, "testdown"));
         
         f = new File("testupload");
         ofs = 0;
         crc = 0;
         try {
            crc=calculateCRC(f);
            ofs = f.length();
         } catch(IOException io) {
            System.out.println("Can't restart, no local file");
         }
         h.sendProtocolPacket(FTPGenerator.upload((byte)0, true, crc, 
                                                  ofs, ofs, "testup"));
      }
   }
   public void fireChangeAreaReply(DSMPBaseHandler h, byte flags, 
                                   byte handle) {
      h.sendProtocolPacket(FTPGenerator.listArea((byte)1));
   }
   public void fireDeleteFileReply(DSMPBaseHandler h, byte flags, 
                                   byte handle) {
   }
   public void fireNewFolderReply(DSMPBaseHandler h, byte flags, byte handle) {
   }
   public void fireUploadReply(DSMPBaseHandler h, byte flags, byte handle, 
                               int id, long ofs) {
      File f = new File("testupload");
      System.out.println(((ofs != 0)?"Restarting ": "Overwriting ") +
                         "testupload");
      ClientUploadOperation op = new ClientUploadOperation(h, id, 
                                                           f.length()-ofs, 
                                                           ofs, f);
      saveOperation(id, op);
      new Thread(op).start();
   }
   public void fireAbortUploadReply(DSMPBaseHandler h, byte flags, 
                                    byte handle, 
                                    int id) {
   }
   public void fireDownloadReply(DSMPBaseHandler h, byte flags, byte handle, 
                                 int id, long ofs, long sz) {
      
      System.out.println(((ofs != 0)?"Restarting ": "Overwriting ") +
                         "testdown");
      File f = new File("testdown");
      Operation op = new ClientDownloadOperation(h, id, sz-ofs, ofs, f);
      saveOperation(id, op);
   }
   public void fireAbortDownloadReply(DSMPBaseHandler h, byte flags, 
                                      byte handle, 
                                      int id) {
   }
   
  /* -------------------------------------------------------*\
  ** Reply Errors
  \* -------------------------------------------------------*/
  
   public void fireGenericReplyError(DSMPBaseHandler h, 
                                     byte flags, byte handle, 
                                     byte opcode, 
                                     short errorcode, String errorStr) {
      
   }
   
   public void fireLoginReplyError(DSMPBaseHandler h, 
                                   byte flags, byte handle, 
                                   short errorcode, String errorStr) {
      
   }
   
   public void fireLogoutReplyError(DSMPBaseHandler h, 
                                    byte flags, byte handle, 
                                    short errorcode, String errorStr) {
      
   }
   
   public void fireListAreaReplyError(DSMPBaseHandler h, 
                                      byte flags, byte handle, 
                                      short errorcode, String errorStr) {
      
   }
   public void fireChangeAreaReplyError(DSMPBaseHandler h, 
                                        byte flags, byte handle, 
                                        short errorcode, String errorStr) {
      
   }
   public void fireDeleteFileReplyError(DSMPBaseHandler h, 
                                        byte flags, byte handle, 
                                        short errorcode, String errorStr) {
      
   }
   public void fireNewFolderReplyError(DSMPBaseHandler h, 
                                       byte flags, byte handle, 
                                       short errorcode, String errorStr) {
      
   }
   public void fireUploadReplyError(DSMPBaseHandler h, 
                                    byte flags, byte handle, 
                                    short errorcode, String errorStr) {
      
   }
   public void fireAbortUploadReplyError(DSMPBaseHandler h, 
                                         byte flags, byte handle, 
                                         short errorcode, String errorStr) {
      
   }
   public void fireDownloadReplyError(DSMPBaseHandler h, 
                                      byte flags, byte handle, 
                                      short errorcode, String errorStr) {
      
   }
   public void fireAbortDownloadReplyError(DSMPBaseHandler h, 
                                           byte flags, byte handle, 
                                           short errorcode, String errorStr) {
      
   }
   
  /* -------------------------------------------------------*\
  ** Events
  \* -------------------------------------------------------*/

   public void fireDownloadFrameEvent(DSMPBaseHandler h, byte flags, 
                                      byte handle, int id, long ofs, 
                                      byte buf[], int bofs, int blen) {
      
      ClientDownloadOperation op = (ClientDownloadOperation)getOperation(id);
      op.frameData(ofs, buf, bofs, blen);
   }
   public void fireAbortDownloadEvent(DSMPBaseHandler h, byte flags, 
                                      byte handle, int id, int reason,
                                      String reasonString) {
   }
   
  /* -------------------------------------------------------*\
  ** Errors
  \* -------------------------------------------------------*/
   
   public void fireUploadDataError(DSMPBaseHandler h, 
                                   byte flags, byte handle, 
                                   int id, short errorcode, 
                                   String errorString) {
   }
   
}
