package oem.edge.ed.odc.ftp.common;

import  oem.edge.ed.odc.dsmp.common.*;

import java.util.*;
import java.io.*;
import java.util.zip.*;
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

public class FTPDispatchBase extends DSMPDispatchBase {

   public  boolean bigdebug                   = true;
   
   
   public byte getReplyForOpcode(byte op) throws InvalidProtocolException {
      byte ret = 0;
      switch(op) {
         case FTPGenerator.OP_LOGIN:
            ret = FTPGenerator.OP_LOGIN_REPLY; break;
         case FTPGenerator.OP_LOGOUT:
            ret = FTPGenerator.OP_LOGOUT_REPLY; break;
         case FTPGenerator.OP_LISTAREA:
            ret = FTPGenerator.OP_LISTAREA_REPLY; break;
         case FTPGenerator.OP_CHANGEAREA:
            ret = FTPGenerator.OP_CHANGEAREA_REPLY; break;
         case FTPGenerator.OP_DELETEFILE:
            ret = FTPGenerator.OP_DELETEFILE_REPLY; break;
         case FTPGenerator.OP_NEWFOLDER:
            ret = FTPGenerator.OP_NEWFOLDER_REPLY; break;
         case FTPGenerator.OP_UPLOAD:
            ret = FTPGenerator.OP_UPLOAD_REPLY; break;
         case FTPGenerator.OP_ABORTUPLOAD:
            ret = FTPGenerator.OP_ABORTUPLOAD_REPLY; break;
         case FTPGenerator.OP_UPLOADDATA:
            throw new 
               InvalidProtocolException("No reply: UploadData Command");
         case FTPGenerator.OP_DOWNLOAD:
            ret = FTPGenerator.OP_DOWNLOAD_REPLY; break;
         case FTPGenerator.OP_ABORTDOWNLOAD:
            ret = FTPGenerator.OP_ABORTDOWNLOAD_REPLY; break;
         case FTPGenerator.OP_OPERATION_COMPLETE:
            throw new 
               InvalidProtocolException("No reply: OperationComplete Command");
         
         default:
            throw new InvalidProtocolException("Invalid CommandCode: " + 
                                               op);
      }
      return ret;
   }
   
   public void operationComplete(Operation op) {
      ;
   }
   
   public int calculateCRC(File f, long inlen) throws IOException {
      FileInputStream fis = new FileInputStream(f);
      byte buf[] = new byte[32768];
      CRC32 crc32 = new CRC32();
      long len = inlen;
      while(len > 0) {
         int r = (int)(len > buf.length ? buf.length : len);
         r = fis.read(buf, 0, r);
//         System.out.println(showbytes(buf, 0, r));
         if (r == -1) {
            throw new IOException("Ran out of bytes before finished CRC");
         }
         crc32.update(buf, 0, r);
         len -= r;
      }
      
      int ret = (int)crc32.getValue();
//      System.out.println("CRC for " + f.getName() + 
//                         " len[" + inlen + "] = " + ret);
      return ret;
   }
   
   public String calculateMD5(File f, long inlen) throws IOException {
      String ret = null;
      byte buf[] = new byte[32768];
      FileInputStream fis = new FileInputStream(f);
      
      try {
         java.security.MessageDigest digest;
         digest = java.security.MessageDigest.getInstance("MD5");
         
         long len = inlen;
         
         if (len <= 0) len = f.length();
         
         while(len > 0) {
            int r = (int)(len > buf.length ? buf.length : len);
            r = fis.read(buf, 0, r);
//         System.out.println(showbytes(buf, 0, r));
            if (r == -1) {
               throw new IOException("Ran out of bytes before finished MD5");
            }
            digest.update(buf, 0, r);
            len -= r;
         }
         
         byte arr[] = digest.digest();
         StringBuffer ans = new StringBuffer();
         for(int i=0 ; i < arr.length; i++) {
            String v = Integer.toHexString(((int)arr[i]) & 0xff);
            if (v.length() == 1) ans.append("0");
            ans.append(v);
         }
         
         ret = ans.toString();
      } catch(java.security.NoSuchAlgorithmException ee) {
         throw new IOException("No MD5 Algo found");
      }
      
      return ret;
   }
   
   public int calculateCRC(File f) throws IOException {
      return calculateCRC(f, f.length());
   }
   public String calculateMD5(File f) throws IOException {
      return calculateMD5(f, f.length());
   }
   
  /* -------------------------------------------------------*\
  ** Shutdown
  \* -------------------------------------------------------*/
   public void fireShutdownEvent(DSMPBaseHandler h) {
      System.out.println("-----fireShutdownEvent connectionid = " + 
                         h.getHandlerId());
      uncaughtProtocol(h, (byte)0);
   }
   

  /* -------------------------------------------------------*\
  ** Commands
  \* -------------------------------------------------------*/
   
   
   public void fireLoginCommandToken(DSMPBaseHandler h, byte flags, 
                                     byte handle, String token) {
      if (printdebug && dodebug) {
         System.out.println("-----fireLoginCommandToken: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\ttoken [" + token + "]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_LOGIN);
   }
   public void fireLoginCommandUserPW(DSMPBaseHandler h, byte flags, 
                                      byte handle, String user, String pw) {
      if (printdebug && dodebug) {
         System.out.println("-----fireLoginCommandUserPW: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tuser [" + user + "]");
         System.out.println("\tpw [" + pw + "]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_LOGIN);
   }
   public void fireLogoutCommand(DSMPBaseHandler h, byte flags, byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireLogoutCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_LOGIN);
   }
   public void fireChangeAreaCommand(DSMPBaseHandler h, byte flags, 
                                     byte handle, String area) {
      if (printdebug && dodebug) {
         System.out.println("-----fireChangeAreaCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]" +
                            "\n\tarea [" +area+"]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_CHANGEAREA);
   }
   public void fireListAreaCommand(DSMPBaseHandler h, byte flags, 
                                   byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireListAreaCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_LISTAREA);
   }
   public void fireDeleteFileCommand(DSMPBaseHandler h, byte flags, 
                                     byte handle, String file) {
      if (printdebug && dodebug) {
         System.out.println("-----fireDeleteFileCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]" +
                            "\n\tarea [" +file+"]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_DELETEFILE);
   }
   public void fireNewFolderCommand(DSMPBaseHandler h, byte flags, 
                                    byte handle, String folder) {
      if (printdebug && dodebug) {
         System.out.println("-----fireNewFolderCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]" +
                            "\n\tfoldr[" +folder+"]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_NEWFOLDER);
   }
   public void fireUploadCommand(DSMPBaseHandler h, byte flags, 
                                 byte handle, boolean tryRestart, 
                                 int crc, long crcSize,
                                 long filelen, String file) {
      if (printdebug && dodebug) {
         System.out.println("-----fireUploadCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\trestart[" +tryRestart+"]");
         System.out.println("\tcrc   [" +crc+"]");
         System.out.println("\tcrclen[" +crcSize+"]");
         System.out.println("\tlen   [" +filelen+"]");
         System.out.println("\tfilen [" +file+"]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_UPLOAD);
   }
   public void fireAbortUploadCommand(DSMPBaseHandler h, byte flags, 
                                      byte handle, int id) {
      if (printdebug && dodebug) {
         System.out.println("-----fireAbortUploadCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tid   [" +id+"]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_ABORTUPLOAD);
   }
   public void fireUploadDataCommand(DSMPBaseHandler h, byte flags, 
                                     byte handle, int id, long ofs, 
                                     CompressInfo ci) {
      if (printdebug && dodebug) {
         System.out.println("-----fireUploadDataCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tid   [" +id+"]");
         System.out.println("\tofs  [" +ofs+"]");
         System.out.println("\tlen  [" +ci.len+"]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_UPLOADDATA);
   }
   public void fireDownloadCommand(DSMPBaseHandler h, byte flags, byte handle, 
                                   boolean tryRestart, int crc, long filelen, 
                                   String file) {
      if (printdebug && dodebug) {
         System.out.println("-----fireDownloadCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\trestart[" +tryRestart+"]");
         System.out.println("\tcrc  [" +crc+"]");
         System.out.println("\tlen  [" +filelen+"]");
         System.out.println("\tfilen[" +file+"]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_DOWNLOAD);
   }
                         
   public void fireAbortDownloadCommand(DSMPBaseHandler h, byte flags, 
                                        byte handle, int id) {
      if (printdebug && dodebug) {
         System.out.println("-----fireAbortDownloadCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tid   [" +id+"]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_ABORTDOWNLOAD);
   }
   
   public void fireOperationCompleteCommand(DSMPBaseHandler h, byte flags, 
                                            byte handle, int id, String md5) {
      if (printdebug && dodebug) {
         System.out.println("-----fireOperationCompleteCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tid   [" +id+"]");
         if ((flags & (byte)0x01) != 0) {
            System.out.println("\tmd5  [" +md5+"]");
         }
      }
      uncaughtProtocol(h, FTPGenerator.OP_OPERATION_COMPLETE);
   }
   
   
  /* -------------------------------------------------------*\
  ** Replies
  \* -------------------------------------------------------*/
  
   public void fireLoginReply(DSMPBaseHandler h, byte flags, byte handle,
                              int  loginid, String area, String sep) {
      if (printdebug && dodebug) {
         System.out.println("-----fireLoginReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tloginid [" + loginid + "]");
         System.out.println("\tarea    [" + area + "]");
         System.out.println("\tsep     [" + sep  + "]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_LOGIN_REPLY);
   }
   
   public void fireLogoutReply(DSMPBaseHandler h, byte flags, byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireLogoutReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_LOGOUT_REPLY);
   }

   public void fireListAreaReply(DSMPBaseHandler h, byte flags, byte handle,
                                 Vector v) {
      if (printdebug && dodebug) {
         System.out.println("-----fireListAreaReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]" +
                            "\n\tentries:");
         
         if (v != null) {
            Enumeration e = v.elements();
            while(e.hasMoreElements()) {
               AreaContent ac = (AreaContent)e.nextElement();
               System.out.println("\t   " + ac.toString());
               
            }
         }
      }
      uncaughtProtocol(h, FTPGenerator.OP_LISTAREA_REPLY);
   }
   public void fireChangeAreaReply(DSMPBaseHandler h, byte flags, 
                                   byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireChangeAreaReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_CHANGEAREA_REPLY);
   }
   public void fireDeleteFileReply(DSMPBaseHandler h, byte flags, 
                                   byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireDeleteFileReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_DELETEFILE_REPLY);
   }
   public void fireNewFolderReply(DSMPBaseHandler h, byte flags, byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireNewFolderReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_NEWFOLDER_REPLY);
   }
   public void fireUploadReply(DSMPBaseHandler h, byte flags, byte handle, 
                               int id, long ofs) {
      if (printdebug && dodebug) {
         System.out.println("-----fireUploadReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]" +
                            "\n\tid   [" + id +"]" +
                            "\n\tofs  [" + ofs + "]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_UPLOAD_REPLY);
   }
   public void fireAbortUploadReply(DSMPBaseHandler h, byte flags, 
                                    byte handle, 
                                    int id) {
      if (printdebug && dodebug) {
         System.out.println("-----fireAbortUploadReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]" +
                            "\n\t" + id);
      }
      uncaughtProtocol(h, FTPGenerator.OP_ABORTUPLOAD_REPLY);
   }
   public void fireDownloadReply(DSMPBaseHandler h, byte flags, byte handle, 
                                 int id, long ofs, long sz) {
      if (printdebug && dodebug) {
         System.out.println("-----fireDownloadReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]" +
                            "\n\tid   [" + id +"]"+
                            "\n\tofs  [" + ofs+"]" +
                            "\n\tsz   [" + sz+"]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_DOWNLOAD_REPLY);
   }
   public void fireAbortDownloadReply(DSMPBaseHandler h, byte flags, 
                                      byte handle, 
                                      int id) {
      if (printdebug && dodebug) {
         System.out.println("-----fireAbortDownloadReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]" +
                            "\n\t" + id);
      }
      uncaughtProtocol(h, FTPGenerator.OP_ABORTDOWNLOAD_REPLY);
   }
   
  /* -------------------------------------------------------*\
  ** Reply Errors
  \* -------------------------------------------------------*/
  
   public void fireGenericReplyError(DSMPBaseHandler h, 
                                     byte flags, byte handle, 
                                     byte opcode, 
                                     short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireGenericReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, opcode);
   }
   
   public void fireLoginReplyError(DSMPBaseHandler h, 
                                   byte flags, byte handle, 
                                   short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireLoginReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_LOGIN_REPLY);
   }
   
   public void fireLogoutReplyError(DSMPBaseHandler h, 
                                    byte flags, byte handle, 
                                    short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireLogoutReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_LOGOUT_REPLY);
   }
   
   public void fireListAreaReplyError(DSMPBaseHandler h, 
                                      byte flags, byte handle, 
                                      short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireListAreaReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_LISTAREA_REPLY);
   }
   public void fireChangeAreaReplyError(DSMPBaseHandler h, 
                                        byte flags, byte handle, 
                                        short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireChangeAreaReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_CHANGEAREA_REPLY);
   }
   public void fireDeleteFileReplyError(DSMPBaseHandler h, 
                                        byte flags, byte handle, 
                                        short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireDeleteFileReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_DELETEFILE_REPLY);
   }
   public void fireNewFolderReplyError(DSMPBaseHandler h, 
                                       byte flags, byte handle, 
                                       short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireNewFolderReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_NEWFOLDER_REPLY);
   }
   public void fireUploadReplyError(DSMPBaseHandler h, 
                                    byte flags, byte handle, 
                                    short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireUploadReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_UPLOAD_REPLY);
   }
   public void fireAbortUploadReplyError(DSMPBaseHandler h, 
                                         byte flags, byte handle, 
                                         short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireAbortUploadReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_ABORTUPLOAD_REPLY);
   }
   public void fireDownloadReplyError(DSMPBaseHandler h, 
                                      byte flags, byte handle, 
                                      short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireDownloadReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_DOWNLOAD_REPLY);
   }
   public void fireAbortDownloadReplyError(DSMPBaseHandler h, 
                                           byte flags, byte handle, 
                                           short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireAbortDownloadReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_ABORTDOWNLOAD_REPLY);
   }
   
  /* -------------------------------------------------------*\
  ** Events
  \* -------------------------------------------------------*/

   public void fireDownloadFrameEvent(DSMPBaseHandler h, byte flags, 
                                      byte handle, int id, long ofs, 
                                      byte buf[], int bofs, int blen) {
      if (printdebug && dodebug) {
         System.out.println("-----fireDownloadFrameEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tid   [" + id + "]");
         System.out.println("\tofs  [" + ofs + "]");
         System.out.println("\tblen [" + blen + "]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_DOWNLOADFRAME_EVENT);
   }
   public void fireAbortDownloadEvent(DSMPBaseHandler h, byte flags, 
                                      byte handle, int id, int reason,
                                      String reasonString) {
      if (printdebug && dodebug) {
         System.out.println("-----fireAbortDownloadEvent: " + 
                            "\n\tflags  [" +flags+"] hand["+handle+"]");
         System.out.println("\tid     [" + id + "]");
         System.out.println("\treason [" + reason + "] = " + reasonString);
      }
      uncaughtProtocol(h, FTPGenerator.OP_ABORTDOWNLOAD_EVENT);
   }
   
   public void fireOperationCompleteEvent(DSMPBaseHandler h, byte flags, 
                                          byte handle, int id, 
                                          String md5, long len) {
      if (printdebug && dodebug) {
         System.out.println("-----fireOperationCompleteEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tid   [" +id+"]");
         if ((flags & (byte)0x01) != 0) {
            System.out.println("\tmd5  [" +md5+"]");
         }
         if ((flags & (byte)0x02) != 0) {
            System.out.println("\tlen  [" +len+"]");
         }
      }
      uncaughtProtocol(h, FTPGenerator.OP_OPERATION_COMPLETE_EVENT);
   }
   
   
  /* -------------------------------------------------------*\
  ** Errors
  \* -------------------------------------------------------*/
   
   public void fireUploadDataError(DSMPBaseHandler h, 
                                   byte flags, byte handle, 
                                   int id, short errorcode, 
                                   String errorString) {
      if (printdebug && dodebug) {
         System.out.println("-----fireUploadDataError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorString + "]");
      }
      uncaughtProtocol(h, FTPGenerator.OP_UPLOADDATA_ERROR);
   }
   
  /* -------------------------------------------------------*\
  ** Checking and Dispatching
  \* -------------------------------------------------------*/
  
   public void dispatchProtocolI(DSMPBaseProto proto, 
                                 DSMPBaseHandler handler, 
                                 boolean doDispatch) 
                                            throws InvalidProtocolException {
                                            
      byte opcode = proto.getOpcode();
      byte flags  = proto.getFlags();
      byte handle = proto.getHandle();
      proto.resetCursor();
      
     // If the caller had originally asked to be synchronized, lets manage that
      boolean syncproto = redispatch && ((flags & 0x40) != 0);
      SynchArguments sargs = null;
      
      if (!FTPGenerator.isValid(opcode)) {
         throw new InvalidProtocolException("Invalid opcode: " + opcode);
      }
      
      if (printdebug && dodebug && redispatch) {
         try {
            if (pdbg == null) {
               pdbg = 
                  (DSMPDispatchBase)this.getClass().getSuperclass().newInstance();
               pdbg.setDebug(true);
               pdbg.setRedispatch(false);
            }
            
            if (bigdebug) {
               System.out.println("==> " + proto.toString());
            }
            pdbg.dispatchProtocolI(proto, null, true);
            proto.resetCursor();
         } catch(Throwable tttt) {
            System.out.println("Error dispatching Debug info = " + tttt.toString());
            tttt.printStackTrace(System.out);
         }
         proto.resetCursor();
      }
      
      if (FTPGenerator.isReply(opcode)) {
         boolean success = (flags & (byte)0x01) != 0;
         if (!success) {
            short    errorcode = proto.getShort();
            String errorString = proto.getString8();
            
            proto.verifyCursorDone();
            if (doDispatch) {
            
               if (syncproto) {
                  sargs = new SynchArguments(handler, proto);
                  sargs.addElement(new Short(errorcode));
                  sargs.addElement(errorString);
               }
            
               switch(opcode) {
                  case FTPGenerator.OP_LOGIN_REPLY:
                     if (syncproto) {
                        handleSynchProto(sargs);
                     } else {
                        fireLoginReplyError(handler, flags, handle,
                                            errorcode, errorString);
                     }
                     break;
                  case FTPGenerator.OP_LOGOUT_REPLY:
                     if (syncproto) {
                        handleSynchProto(sargs);
                     } else {
                        fireLogoutReplyError(handler, flags, handle,
                                             errorcode, errorString);
                     }
                     break;
                  case FTPGenerator.OP_LISTAREA_REPLY:
                     if (syncproto) {
                        handleSynchProto(sargs);
                     } else {
                        fireListAreaReplyError(handler, flags, handle,
                                               errorcode, errorString);
                     }
                     break;
                  case FTPGenerator.OP_CHANGEAREA_REPLY:
                     if (syncproto) {
                        handleSynchProto(sargs);
                     } else {
                        fireChangeAreaReplyError(handler, flags, handle,
                                                 errorcode, errorString);
                     }
                     break;
                  case FTPGenerator.OP_DELETEFILE_REPLY:
                     if (syncproto) {
                        handleSynchProto(sargs);
                     } else {
                        fireDeleteFileReplyError(handler, flags, handle,
                                                 errorcode, errorString);
                     }
                     break;
                  case FTPGenerator.OP_NEWFOLDER_REPLY:
                     if (syncproto) {
                        handleSynchProto(sargs);
                     } else {
                        fireNewFolderReplyError(handler, flags, handle,
                                                errorcode, errorString);
                     }
                     break;
                  case FTPGenerator.OP_UPLOAD_REPLY:
                     if (syncproto) {
                        handleSynchProto(sargs);
                     } else {
                        fireUploadReplyError(handler, flags, handle,
                                             errorcode, errorString);
                     }
                     break;
                  case FTPGenerator.OP_ABORTUPLOAD_REPLY:
                     if (syncproto) {
                        handleSynchProto(sargs);
                     } else {
                        fireAbortUploadReplyError(handler, flags, handle,
                                                  errorcode, errorString);
                     }
                     break;
                  case FTPGenerator.OP_DOWNLOAD_REPLY:
                     if (syncproto) {
                        handleSynchProto(sargs);
                     } else {
                        fireDownloadReplyError(handler, flags, handle,
                                               errorcode, errorString);
                     }
                     break;
                  case FTPGenerator.OP_ABORTDOWNLOAD_REPLY:
                     if (syncproto) {
                        handleSynchProto(sargs);
                     } else {
                        fireAbortDownloadReplyError(handler, flags, handle,
                                                    errorcode, errorString);
                     }
                     break;
               }
            }
         } else {
            switch(opcode) {
               case FTPGenerator.OP_LOGIN_REPLY: {
                  
                  int loginid  = proto.getInteger();
                  String area  = proto.getString16();
                  String sep   = proto.getString8();
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(new Integer(loginid));
                        sargs.addElement(area);
                        sargs.addElement(sep);
                        handleSynchProto(sargs);
                     } else {
                        fireLoginReply(handler, flags, handle, loginid, 
                                       area, sep);
                     }
                  }
                  break;
               }
               case FTPGenerator.OP_LOGOUT_REPLY:
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        handleSynchProto(sargs);
                     } else {
                        fireLogoutReply(handler, flags, handle);
                     }
                  }
                  break;
               case FTPGenerator.OP_LISTAREA_REPLY: {
                  Vector v = null;
                  int num = proto.get3ByteInteger();
                  if (num > 0) {
                     v = new Vector();
                     while(num-- > 0) {
                        AreaContent ac = new AreaContent();
                        ac.setType(proto.getByte());
                        ac.setTimeDate(proto.getLong());
                        ac.setSize(proto.getLong());
                        ac.setArea(proto.getString16());
                        ac.setName(proto.getString16());
                        v.addElement(ac);
                     }
                  }
                  
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(v);
                        handleSynchProto(sargs);
                     } else {
                        fireListAreaReply(handler, flags, handle, v);
                     }
                  }
                  break;
               }
               case FTPGenerator.OP_CHANGEAREA_REPLY:
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        handleSynchProto(sargs);
                     } else {
                        fireChangeAreaReply(handler, flags, handle);
                     }
                  }
                  break;
               case FTPGenerator.OP_DELETEFILE_REPLY:
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        handleSynchProto(sargs);
                     } else {
                        fireDeleteFileReply(handler, flags, handle);
                     }
                  }
                  break;
               case FTPGenerator.OP_NEWFOLDER_REPLY:
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        handleSynchProto(sargs);
                     } else {
                        fireNewFolderReply(handler, flags, handle);
                     }
                  }
                  break;
               case FTPGenerator.OP_UPLOAD_REPLY: {
                  int  id  = proto.getInteger();
                  long ofs = proto.getLong();
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(new Integer(id));
                        sargs.addElement(new Long(ofs));
                        handleSynchProto(sargs);
                     } else {
                        fireUploadReply(handler, flags, handle, id, ofs);
                     }
                  }
                  break;
               }
               case FTPGenerator.OP_ABORTUPLOAD_REPLY: {
                  int id = proto.getInteger();
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(new Integer(id));
                        handleSynchProto(sargs);
                     } else {
                        fireAbortUploadReply(handler, flags, handle, id);
                     }
                  }
                  break;
               }
               case FTPGenerator.OP_DOWNLOAD_REPLY: {
                  int id   = proto.getInteger();
                  long ofs = proto.getLong();
                  long sz  = proto.getLong();
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(new Integer(id));
                        sargs.addElement(new Long(ofs));
                        sargs.addElement(new Long(sz));
                        handleSynchProto(sargs);
                     } else {
                        fireDownloadReply(handler, flags, handle, id, ofs, sz);
                     }
                  }
                  break;
               }
               case FTPGenerator.OP_ABORTDOWNLOAD_REPLY: {
                  int id = proto.getInteger();
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     if (syncproto) { 
                        sargs = new SynchArguments(handler, proto);
                        sargs.addElement(new Integer(id));
                        handleSynchProto(sargs);
                     } else {
                        fireAbortDownloadReply(handler, flags, handle, id);
                     }
                  }
                  break;
               }
            }
         }
      } else if (FTPGenerator.isError(opcode)) {
         int extra          = proto.getExtraInt();
         short errorcode    = proto.getShort();
         String errorString = proto.getString8();
         proto.verifyCursorDone();
         if (doDispatch) {
            switch(opcode) {
               case FTPGenerator.OP_UPLOADDATA_ERROR:
                  fireUploadDataError(handler, flags, handle, extra,
                                      errorcode, errorString);
                  break;
            }
         }
      } else if (FTPGenerator.isEvent(opcode)) {
         
         switch(opcode) {
            case FTPGenerator.OP_DOWNLOADFRAME_EVENT: {
               int id          = proto.getInteger();
               long ofs        = proto.getLong();
               CompressInfo ci = proto.getDataAtCursor();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireDownloadFrameEvent(handler, flags, handle, 
                                         id, ofs, ci.buf, ci.ofs, ci.len);
               }
               break;
            }
            case FTPGenerator.OP_ABORTDOWNLOAD_EVENT: {
               int    id          = proto.getInteger();
               int    reason      = proto.getInteger();
               String reasonStr   = proto.getString16();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireAbortDownloadEvent(handler, flags, handle, id, 
                                         reason, reasonStr);
               }
               break;
            }
            case FTPGenerator.OP_OPERATION_COMPLETE_EVENT: {
               int  id = proto.getInteger();
               
               String md5 = "";
               if ((flags & (byte)0x01) != 0) {
                  md5 = proto.getString8();
               }
               
               long len = -1;
               if ((flags & (byte)0x02) != 0) {
                  len = proto.getLong();
               }
               
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireOperationCompleteEvent(handler, flags, handle, id, 
                                             md5, len);
               }
               break;
            }
         }            
      } else if (FTPGenerator.isCommand(opcode)) {
      
         switch(opcode) {
            case FTPGenerator.OP_LOGIN: {
               String tokenUser = proto.getString16();
               String pw        = proto.getString8();
               proto.verifyCursorDone();
               if (doDispatch) {
                  if ((flags & (byte)0x01) != 0) {
                     fireLoginCommandToken(handler, flags, handle, tokenUser);
                  } else {
                     fireLoginCommandUserPW(handler, flags, handle, 
                                            tokenUser.toLowerCase(), pw);
                  }
               }
               break;
            }
            case FTPGenerator.OP_LOGOUT: {
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireLogoutCommand(handler, flags, handle);
               }
               break;
            }
            case FTPGenerator.OP_CHANGEAREA: {
               String area = proto.getString16();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireChangeAreaCommand(handler, flags, handle, area);
               }
               break;
            }
            case FTPGenerator.OP_LISTAREA: {
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireListAreaCommand(handler, flags, handle);
               }
               break;
            }
            case FTPGenerator.OP_DELETEFILE: {
               String file = proto.getString16();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireDeleteFileCommand(handler, flags, handle, file);
               }
               break;
            }
            case FTPGenerator.OP_NEWFOLDER: {
               String folder = proto.getString16();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireNewFolderCommand(handler, flags, handle, folder);
               }
               break;
            }
            case FTPGenerator.OP_UPLOAD: {
               int  crc     = proto.getInteger();
               long crcsz   = proto.getLong();
               long filelen = proto.getLong();
               String file  = proto.getString16();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireUploadCommand(handler, flags, handle, 
                                    (flags & (byte)1) != 0, crc, crcsz,
                                    filelen, file);
               }
               break;
            }
            case FTPGenerator.OP_ABORTUPLOAD: {
               int  id = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireAbortUploadCommand(handler, flags, handle, id);
               }
               break;
            }
            case FTPGenerator.OP_UPLOADDATA: {
               int  id         = proto.getInteger();
               long ofs        = proto.getLong();
               CompressInfo ci = proto.getDataAtCursor();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireUploadDataCommand(handler, flags, handle,
                                        id, ofs, ci);
               }
               break;
            }
            case FTPGenerator.OP_DOWNLOAD: {
               int  crc     = proto.getInteger();
               long filelen = proto.getLong();
               String file  = proto.getString16();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireDownloadCommand(handler, flags, handle, 
                                      (flags & (byte)1) != 0, 
                                      crc, filelen, file);
               }
               break;
            }
            case FTPGenerator.OP_ABORTDOWNLOAD: {
               int  id = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireAbortDownloadCommand(handler, flags, handle, id);
               }
               break;
            }
            case FTPGenerator.OP_OPERATION_COMPLETE: {
               int  id = proto.getInteger();
               String md5 = "";
               if ((flags & (byte)0x01) != 0) {
                  md5 = proto.getString8();
               }
               
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireOperationCompleteCommand(handler, flags, handle, id, md5);
               }
               break;
            }
         }
      
      } else {
         throw new InvalidProtocolException(opcode + ": Bad Opcode");
      }
   }
}
