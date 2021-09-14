package oem.edge.ed.odc.dropbox.client;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.ftp.client.*;
import  oem.edge.ed.odc.dsmp.common.*;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.net.*;

public class TestDropbox extends DropboxDispatchBase {

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
                               "TestDropbox [-host hostname portno] [-userpw user pw] [-token token]");
            return false;
         }
      }
      return true;
   }
   
   public static void main(String args[]) {
      TestDropbox client = new TestDropbox();
      client.domain(args);
   }
   
   
   public void domain(String args[]) {
   
      setDebug(true);
      byte hand = 1;
      if (!parseArgs(args)) return;
      try {
         DSMPBaseHandler handler = new DSMPSocketHandler(servername, 
                                                         localport, 
                                                         this);
         DSMPBaseProto proto;
         if (user != null) {
            proto = DropboxGenerator.loginUserPW(hand++, user, pw);
         } else if (token != null) {
            proto = DropboxGenerator.loginToken(hand++, token);
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
      h.sendProtocolPacket(DropboxGenerator.queryPackages((byte)0, null, true));
   }
   
   public void fireLogoutReply(DSMPBaseHandler h, byte flags, byte handle) {
      System.exit(3);
   }

   public void fireUploadReply(DSMPBaseHandler h, byte flags, byte handle, 
                               int id, long ofs) {
   }
//   public void fireAbortUploadReply(DSMPBaseHandler h, byte flags, 
//                                    byte handle, 
//                                   int id) {
//   }
   public void fireDownloadReply(DSMPBaseHandler h, byte flags, byte handle, 
                                 int id, long ofs, long sz) {
      
      System.out.println(((ofs != 0)?"Restarting ": "Overwriting ") +
                         "testdown");
      File f = new File("testdown");
      Operation op = new ClientDownloadOperation(h, id, sz-ofs, ofs, f);
      saveOperation(id, op);
   }
//   public void fireAbortDownloadReply(DSMPBaseHandler h, byte flags, 
//                                      byte handle, 
//                                      int id) {
//   }
   
   
   public void fireCreatePackageReply(DSMPBaseHandler h, 
                                      byte flags, byte handle, 
                                      long id) {
      File f = new File("testupload");
      long ofs = 0;
      String md5 = "";
      try {
         md5=calculateMD5(f);
         ofs = f.length();
      } catch(IOException io) {
         System.out.println("Can't restart, no local file");
      }
      h.sendProtocolPacket(DropboxGenerator.uploadFileToPackage((byte)0, 
                                                                id,
                                                                true, md5, 
                                                                ofs, ofs, 
                                                                "testup"));
   }

   public void fireDeletePackageReply(DSMPBaseHandler h, 
                                      byte flags, byte handle) {
      
   }

   public void fireCommitPackageReply(DSMPBaseHandler h, 
                                      byte flags, byte handle) {
      h.sendProtocolPacket(DropboxGenerator.queryPackages((byte)2, null, true)); 
      
   }

   long pid = -1;
   public void fireQueryPackagesReply(DSMPBaseHandler h, 
                                      byte flags, byte handle, 
                                      boolean ownerOrAccessor, 
                                      Vector vec) {
      if (handle == 0) {
         h.sendProtocolPacket(DropboxGenerator.createPackage((byte)0, 
                                                             "testpackage",
                                                             null));
      } else if (handle == 1) {
         PackageInfo pi = (PackageInfo)vec.elementAt(0);
         
         pid = pi.getPackageId();
         h.sendProtocolPacket(
            DropboxGenerator.queryPackageContents((byte)0, pid));
      }
   }

   public void fireQueryPackageReply(DSMPBaseHandler h, 
                                     byte flags, byte handle, 
                                     PackageInfo info) {
   }

   public void fireQueryPackageContentsReply(DSMPBaseHandler h, 
                                             byte flags, byte handle, 
                                             long expire, long size,
                                             Vector vec) {
      h.sendProtocolPacket(
         DropboxGenerator.commitPackage((byte)0, pid));
   }

   public void fireQueryPackageAclsReply(DSMPBaseHandler h, 
                                         byte flags, byte handle, 
                                         Vector vec) {
      
   }

   public void fireQueryPackageFileAclsReply(DSMPBaseHandler h, 
                                             byte flags, byte handle, 
                                             Vector vec) {
      
   }

   public void fireQueryFilesReply(DSMPBaseHandler h, 
                                   byte flags, byte handle,
                                   boolean ownerAccessor, Vector vec) {
      
   }

   public void fireQueryFileReply(DSMPBaseHandler h, 
                                  byte flags, byte handle, 
                                  String name, byte status, long size, 
                                  Vector vec) {
      
   }

   public void fireAddItemToPackageReply(DSMPBaseHandler h, 
                                         byte flags, byte handle) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireAddItemToPackageReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DropboxGenerator.OP_ADD_ITEM_TO_PACKAGE_REPLY);
   }

   public void fireUploadFileToPackageReply(DSMPBaseHandler h, 
                                            byte flags, byte handle, 
                                            long itemid, boolean isRestarted,
                                            int opid, long ofs) {
      File f = new File("testupload");
      System.out.println(((ofs != 0)?"Restarting ": "Overwriting ") +
                         "testupload");
      ClientUploadOperation op = new ClientUploadOperation(h, opid, 
                                                           f.length()-ofs, 
                                                           ofs, f);
      saveOperation(opid, op);
      new Thread(op).start();
   }

   public void fireRemoveItemFromPackageReply(DSMPBaseHandler h, 
                                              byte flags, byte handle) {
      
   }

   public void fireDownloadPackageItemReply(DSMPBaseHandler h, 
                                            byte flags, byte handle, 
                                            int opid, long ofs, long size) {
      
   }

   public void fireAddPackageAclReply(DSMPBaseHandler h, 
                                      byte flags, byte handle) { 
      
   }

   public void fireRemovePackageAclReply(DSMPBaseHandler h, 
                                         byte flags, byte handle) {
      
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
   
   public void fireOperationCompleteEvent(DSMPBaseHandler h, 
                                          byte flags, byte handle, 
                                          int id) {
      
      h.sendProtocolPacket(DropboxGenerator.queryPackages((byte)1, null, true)); 
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
