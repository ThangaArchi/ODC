package oem.edge.ed.odc.ftp.common;

import  oem.edge.ed.odc.dsmp.common.*;

import java.lang.*;
import java.util.*;

public class FTPGenerator {

  // Using bit7 is offlimits! Used for proto-morphing
   public final static byte FLAGS_BIT0                 = (byte)0x01;
   public final static byte FLAGS_BIT1                 = (byte)0x02;
   public final static byte FLAGS_BIT2                 = (byte)0x04;
   public final static byte FLAGS_BIT3                 = (byte)0x08;
   public final static byte FLAGS_BIT4                 = (byte)0x10;
   public final static byte FLAGS_BIT5                 = (byte)0x20;
   public final static byte FLAGS_BIT6                 = (byte)0x40;
   public final static byte FLAGS_XTRAINT              = (byte)0x80;

   public final static byte OPCOMMAND                  = (byte)1;
   public final static byte OPREPLY                    = (byte)41;
   public final static byte OPEVENT                    = (byte)81;
   public final static byte OPERROR                    = (byte)101;

   public final static byte OP_LOGIN                   = (OPCOMMAND+1);
   public final static byte OP_LOGOUT                  = (OPCOMMAND+2);
   public final static byte OP_LISTAREA                = (OPCOMMAND+3);
   public final static byte OP_CHANGEAREA              = (OPCOMMAND+4);
   public final static byte OP_DELETEFILE              = (OPCOMMAND+5);
   public final static byte OP_NEWFOLDER               = (OPCOMMAND+6);
   public final static byte OP_UPLOAD                  = (OPCOMMAND+7);
   public final static byte OP_ABORTUPLOAD             = (OPCOMMAND+8);
   public final static byte OP_UPLOADDATA              = (OPCOMMAND+9);
   public final static byte OP_DOWNLOAD                = (OPCOMMAND+10);
   public final static byte OP_ABORTDOWNLOAD           = (OPCOMMAND+11);
   public final static byte OP_OPERATION_COMPLETE      = (OPCOMMAND+12);
   public final static byte OPLASTCOMMAND              = (OP_OPERATION_COMPLETE);
   
   public final static byte OP_LOGIN_REPLY             = (OPREPLY  +1);
   public final static byte OP_LOGOUT_REPLY            = (OPREPLY  +2);
   public final static byte OP_LISTAREA_REPLY          = (OPREPLY  +3);
   public final static byte OP_CHANGEAREA_REPLY        = (OPREPLY  +4);
   public final static byte OP_DELETEFILE_REPLY        = (OPREPLY  +5);
   public final static byte OP_NEWFOLDER_REPLY         = (OPREPLY  +6);
   public final static byte OP_UPLOAD_REPLY            = (OPREPLY  +7);
   public final static byte OP_ABORTUPLOAD_REPLY       = (OPREPLY  +8);
   public final static byte OP_DOWNLOAD_REPLY          = (OPREPLY  +9);
   public final static byte OP_ABORTDOWNLOAD_REPLY     = (OPREPLY  +10);
   public final static byte OPLASTREPLY                
                                        = (OP_ABORTDOWNLOAD_REPLY);
   
   public final static byte OP_DOWNLOADFRAME_EVENT     = (OPEVENT  +1);
   public final static byte OP_ABORTDOWNLOAD_EVENT     = (OPEVENT  +2);
   public final static byte OP_OPERATION_COMPLETE_EVENT= (OPEVENT  +3);
   public final static byte OPLASTEVENT                
                                        = (OP_OPERATION_COMPLETE_EVENT);

   public final static byte OP_UPLOADDATA_ERROR        = (OPERROR  +1);
   public final static byte OPLASTERROR                = (OP_UPLOADDATA_ERROR);
   
   static public boolean isError(byte op) {
      return op >= OPERROR && op <= OPLASTERROR;
   }
   static public boolean isReply(byte op) {
      return op >= OPREPLY && op <= OPLASTREPLY;
   }
   static public boolean isEvent(byte op) {
      return op >= OPEVENT && op <= OPLASTEVENT;
   }
   static public boolean isCommand(byte op) {
      return op >= OPCOMMAND && op <= OPLASTCOMMAND;
   }
   
   static public boolean isValid(byte op) {
      return isCommand(op) || isReply(op) || isEvent(op) || isError(op);
   }

   public static String opcodeToString(byte opcode) {
      String s = null;
      switch(opcode) {
         case OP_LOGIN:
            s = "LOGIN"; break;
         case OP_LOGOUT:
            s = "LOGOUT"; break;
         case OP_LISTAREA:
            s = "LISTAREA"; break;
         case OP_CHANGEAREA:
            s = "CHANGEAREA"; break;
         case OP_DELETEFILE:
            s = "DELETEFILE"; break;
         case OP_NEWFOLDER:
            s = "NEWFOLDER"; break;
         case OP_UPLOAD:
            s = "UPLOAD"; break;
         case OP_ABORTUPLOAD:
            s = "ABORTUPLOAD"; break;
         case OP_UPLOADDATA:
            s = "UPLOADDATA"; break;
         case OP_DOWNLOAD:
            s = "DOWNLOAD"; break;
         case OP_ABORTDOWNLOAD:
            s = "ABORTDOWNLOAD"; break;
         case OP_OPERATION_COMPLETE:
            s = "OPERATION_COMPLETE"; break;
         case OP_LOGIN_REPLY:
            s = "LOGIN_REPLY"; break;
         case OP_LOGOUT_REPLY:
            s = "LOGOUT_REPLY"; break;
         case OP_LISTAREA_REPLY:
            s = "LISTAREA_REPLY"; break;
         case OP_CHANGEAREA_REPLY:
            s = "CHANGEAREA_REPLY"; break;
         case OP_DELETEFILE_REPLY:
            s = "DELETEFILE_REPLY"; break;
         case OP_UPLOAD_REPLY:
            s = "UPLOAD_REPLY"; break;
         case OP_ABORTUPLOAD_REPLY:
            s = "ABORTUPLOAD_REPLY"; break;
         case OP_DOWNLOAD_REPLY:
            s = "DOWNLOAD_REPLY"; break;
         case OP_ABORTDOWNLOAD_REPLY:
            s = "ABORTDOWNLOAD_REPLY"; break;
         case OP_DOWNLOADFRAME_EVENT:
            s = "DOWNLOADFRAME_EVENT"; break;
         case OP_ABORTDOWNLOAD_EVENT:
            s = "ABORTDOWNLOAD_EVENT"; break;
         case OP_OPERATION_COMPLETE_EVENT:
            s = "OPERATION_COMPLETE_EVENT"; break;
         case OP_UPLOADDATA_ERROR:
            s = "UPLOADDATA_ERROR"; break;
         default: 
            s = "Invalid Opcode: " + opcode; break;
      }
      return s;
   }
   
   
  /*--------------------------------------------------------*\
  ** Generic Reply Error
  \*--------------------------------------------------------*/
   public static DSMPBaseProto genericReplyError(byte opcode, byte handle, 
                                             int errorcode, String msg) {
      DSMPBaseProto ret = new DSMPBaseProto(opcode, (byte)0x00, handle);
      ret.appendShort(errorcode);
      ret.appendString8(msg!=null?msg:"");
      return ret;
   }
   
  /*--------------------------------------------------------*\
  ** Generic Error
  \*--------------------------------------------------------*/
   public static DSMPBaseProto genericError(byte handle, byte opcode,
                                            int errorcode, String msg) {
      
      DSMPBaseProto ret = new DSMPBaseProto(opcode, (byte)0, handle);
      ret.appendShort(errorcode);
      ret.appendString8(msg!=null?msg:"");
      return ret;
   }
   
   
  /*--------------------------------------------------------*\
  ** Login/Logout
  \*--------------------------------------------------------*/
  
  /*
  **   ======================================================================
  **   Login                  -  Log into Transfer system
  **      
  **      Flags   : bit0 set data area contains TOKEN describing user 
  **                properties otherwise data area contains userid & pw
  **
  **      String16: Token or Username
  **      String8 : PW
  **
  **   LoginReply             - 
  **
  **      Flags   : Bit0 set if success
  **
  **      Integer : login ID# if success
  **      String16: area string
  **      String8 : file separator string
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   ======================================================================
  **   Logout                 -  Log out of system, abort any operations, etc
  **
  **      None
  ** 
  **   LogoutReply
  **      
  **      Flags   : Bit0 set if success.
  **                Connection will be closed by server soon after
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **   ======================================================================
  */
  
   public static DSMPBaseProto loginToken(byte handle, String token) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_LOGIN, (byte)0x01, handle);
      ret.appendString16(token);
      ret.appendString8("");
      return ret;
   }
   
   public static DSMPBaseProto loginUserPW(byte handle, String user, 
                                           String pw) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_LOGIN, (byte)0x00, handle);
      ret.appendString16(user);
      ret.appendString8(pw);
      return ret;
   }
   
   public static DSMPBaseProto loginReply(byte handle, int loginid,
                                          String area, String filesep) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_LOGIN_REPLY, (byte)0x01, 
                                            handle);
      ret.appendInteger(loginid);
      ret.appendString16(area);
      ret.appendString8(filesep);
      return ret;
   }
   
   public static DSMPBaseProto logout(byte handle) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_LOGOUT, (byte)0x00, handle);
      return ret;
   }
   
   public static DSMPBaseProto logoutReply(byte handle) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_LOGOUT_REPLY, (byte)0x01, 
                                            handle);
      return ret;
   }
   
   
  /*--------------------------------------------------------*\
  ** ChangeArea, ListArea, DeleteFile, NewFolder
  \*--------------------------------------------------------*/
  /*   
  **   ======================================================================
  **   ChangeArea             -  Essentially, change directory
  **
  **      Flags   : None
  **      
  **      String16: new area String
  **      
  **   ChangeAreaReply        -  Success or failure for change
  **
  **      Flags   : Bit0 set if success.
  ** 
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   ======================================================================
  **   ListArea               -  List contents of current area
  **
  **      Flags   : None 
  **
  **      
  **   ListAreaReply          -  Returns contents of List operation
  **
  **      Flags   : Bit0 set if success.
  **
  **      Integer3: num entries
  ** 
  **    x Byte    : type (1 = file, 2 = dir)
  **    x Long    : timedate (lastmodified)
  **    x Long    : size
  **    x String16: area name
  **    x String16: name
  **       
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   ======================================================================
  **   DeleteFile             -  delete the specified 'file'
  **      
  **      Flags   : None
  **      
  **      String16: 'file' name (no path info). Must be in current area
  **      
  **   DeleteFileReply        -  Success or failure for delete
  **
  **      Flags   : Bit0 set if success.
  ** 
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   ======================================================================
  **   NewFolder              -  Create a new 'folder'
  **      
  **      Flags   : None
  **      
  **      String16: New 'folder' name (to be created in current area)
  **      
  **   NewFolderReply         -  Success or failure for create
  **
  **      Flags   : Bit0 set if success.
  ** 
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **   ======================================================================
  */   
   
   public static DSMPBaseProto changeArea(byte handle, String newarea) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_CHANGEAREA, (byte)0x00, handle);
      ret.appendString16(newarea);
      return ret;
   }
   
   public static DSMPBaseProto changeAreaReply(byte handle) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_CHANGEAREA_REPLY, (byte)0x01,
                                            handle);
      return ret;
   }
   
   public static DSMPBaseProto listArea(byte handle) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_LISTAREA, (byte)0x00, handle);
      return ret;
   }
   
   public static DSMPBaseProto listAreaReply(byte handle, Vector v) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_LISTAREA_REPLY, (byte)0x01,
                                            handle);
      int num = (v == null)?0:v.size();
      ret.append3ByteInteger(num);
      if (num != 0) {
         Enumeration e = v.elements();
         while(e.hasMoreElements()) {
            AreaContent ac = (AreaContent)e.nextElement();
            ret.appendByte(ac.getType());
            ret.appendLong(ac.getTimeDate());
            ret.appendLong(ac.getSize());
            ret.appendString16(ac.getArea());
            ret.appendString16(ac.getName());
         }
      }
      return ret;
   }
   
   public static DSMPBaseProto deleteFile(byte handle, String toDelete) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_DELETEFILE, (byte)0x00, handle);
      ret.appendString16(toDelete);
      return ret;
   }
   
   public static DSMPBaseProto deleteFileReply(byte handle) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_DELETEFILE_REPLY, (byte)0x01,
                                            handle);
      return ret;
   }
   
   public static DSMPBaseProto newFolder(byte handle, String folder) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_NEWFOLDER, (byte)0x00, handle);
      ret.appendString16(folder);
      return ret;
   }
   
   public static DSMPBaseProto newFolderReply(byte handle) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_NEWFOLDER_REPLY, (byte)0x01,
                                            handle);
      return ret;
   }
   
  /*--------------------------------------------------------*\
  ** Upload, AbortUpload, UploadData, UploadDataError
  \*--------------------------------------------------------*/
  /*   
  **   ======================================================================
  **   Upload                 -  Begin Upload operation
  **      
  **      Flags   : Bit0 set if restart is requested (CRC is valid below)
  ** 
  **      Integer : CRC for restart (using implied length of server file)
  **      Long    : crc length
  **      Long    : file length
  **      String16: String 'filename' to upload to current area
  **      
  **      
  **   UploadReply            -  Success or failure for upload start
  **
  **      Flags   : Bit0 set if success.
  **                Bit1 set if restartable
  ** 
  **      Integer : ID for upload operation
  **      Long    : starting offset  (will be 0 if restart not possible)
  **
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   ======================================================================
  **   AbortUpload            -  Abort the Upload operation indicated 
  **
  **      Flags   : None
  **
  **      Integer : ID for upload
  **      
  **   AbortUploadReply       -  
  **
  **      Flags   : Bit0 set if success.
  **
  **      Integer : ID for upload
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   ======================================================================
  **   UploadData             -  Data for frame in upload process
  **      
  **      Flags   : None
  **
  **      Integer : Upload ID
  **      Long    : data ofs
  **    x Byte[]  : frame data
  ** 
  **      
  **   UploadDataError        -  If error uploading data
  **
  **      Flags   : None
  ** 
  **      xtraInt : uploadID
  **
  **      Short   : errorcode
  **      String8 : contains error indicator
  **
  **   ======================================================================
  */   
   
   public static DSMPBaseProto upload(byte handle, boolean tryRestart, 
                                      int crc, long crcSize,
                                      long size, String filename) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_UPLOAD, (byte)(tryRestart?1:0),
                                            handle);
      ret.appendInteger(crc);
      ret.appendLong(crcSize);
      ret.appendLong(size);
      ret.appendString16(filename);
      return ret;
   }
   
   public static DSMPBaseProto uploadReply(byte handle, int id, long ofs) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_UPLOAD_REPLY, 
                                            (byte)((ofs>0)?3:1), handle);
      ret.appendInteger(id);
      ret.appendLong(ofs);
      return ret;
   }
   
   public static DSMPBaseProto abortUpload(byte handle, int id) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_ABORTUPLOAD, (byte)0, handle);
      ret.appendInteger(id);
      return ret;
   }
   
   public static DSMPBaseProto abortUploadReply(byte handle, int id) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_ABORTUPLOAD_REPLY, (byte)1, handle);
      ret.appendInteger(id);
      return ret;
   }
   
   public static DSMPBaseProto uploadData(byte handle, int id, long ofs, 
                                          byte buf[], int bofs, int blen) {
      DSMPBaseProto ret = new DSMPBaseProto(blen+12, OP_UPLOADDATA, 
                                            (byte)0x00, handle);
      ret.appendInteger(id);
      ret.appendLong(ofs);
      ret.appendData(buf, bofs, blen);
      
      return ret;
   }
   
   public static DSMPBaseProto uploadDataError(byte handle, 
                                               int errorcode, String msg, 
                                               int id) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_UPLOADDATA_ERROR, (byte)0, 
                                            handle);
      ret.setExtraInt(id);
      ret.appendShort(errorcode);
      ret.appendString8(msg!=null?msg:"");
      return ret;
   }
   
  /*--------------------------------------------------------*\
  ** Download, AbortDownload, DownloadFrameEvent AbortDownloadEvent
  \*--------------------------------------------------------*/
  /*   
  **   ======================================================================
  **   Download               -  Begin Download operation
  **      
  **      Flags   : Bit0 set if restart is requested (CRC/len valid below)
  ** 
  **      Integer : CRC for restart
  **      Long    : file length
  **      String16: String 'filename' to download from current area
  **      
  **      
  **   DownloadReply          -  Success or failure for download start
  **
  **      Flags   : Bit0 set if success.
  **                Bit1 set if restartable
  ** 
  **      Integer : ID for download operation
  **      Long    : starting offset  (will be 0 if restart not possible)
  **      Long    : total file size
  **
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   ======================================================================
  **   AbortDownload          -  Abort the Download operation indicated 
  **
  **      Flags   : None
  **
  **      Integer : ID for download
  **      
  **   AbortDownloadReply     -  
  **
  **      Flags   : Bit0 set if success.
  **
  **      Integer : ID for download
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   ======================================================================
  **   DownloadFrameEvent     -  Data for frame in upload process
  **      
  **      Flags   : None
  **
  **      Integer : Download ID
  **      Long    : data ofs
  **      Byte[]  : frame data
  ** 
  **      
  **   AbortDownloadEvent     -  Sent if server self aborts the download
  **
  **      Flags   : None
  ** 
  **      Integer : downloadID
  **      Integer : reasonCode
  **      String16: reason text
  **
  **   ======================================================================
  */   
   
   public static DSMPBaseProto download(byte handle, boolean tryRestart, 
                                        int crc, long size, String filename) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_DOWNLOAD,
                                            (byte)(tryRestart?1:0), handle);
      ret.appendInteger(crc);
      ret.appendLong(size);
      ret.appendString16(filename);
      return ret;
   }
   
   public static DSMPBaseProto downloadReply(byte handle, int id, 
                                             long ofs, long sz) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_DOWNLOAD_REPLY, 
                                            (byte)((ofs>0)?3:1), handle);
      ret.appendInteger(id);
      ret.appendLong(ofs);
      ret.appendLong(sz);
      return ret;
   }
   
   public static DSMPBaseProto abortDownload(byte handle, int id) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_ABORTDOWNLOAD, (byte)0, handle);
      ret.appendInteger(id);
      return ret;
   }
   
   public static DSMPBaseProto abortDownloadReply(byte handle, int id) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_ABORTDOWNLOAD_REPLY, 
                                            (byte)1, handle);
      ret.appendInteger(id);
      return ret;
   }
   
   public static DSMPBaseProto downloadFrameEvent(byte handle, int id, 
                                                  long ofs, 
                                                  byte buf[], int bofs, 
                                                  int blen) {
      DSMPBaseProto ret = new DSMPBaseProto(blen+12, OP_DOWNLOADFRAME_EVENT,
                                            (byte)0x00, handle);
      ret.appendInteger(id);
      ret.appendLong(ofs);
      ret.appendData(buf, bofs, blen);
      
      return ret;
   }
   
   public static DSMPBaseProto abortDownloadEvent(byte handle, int id,
                                                  int reason, String resStr) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_ABORTDOWNLOAD_EVENT, (byte)0, 
                                            handle);
      ret.appendInteger(id);
      ret.appendInteger(reason);
      ret.appendString16(resStr);
      return ret;
   }
  /* 
  **   ======================================================================
  **   OperationComplete, OperationCompleteEvent -  Transfer operation complete
  **      
  **   OperationComplete      -  Download operation complete
  ** 
  **      Flags   : None
  **
  **      Flags   : Bit0 set if MD5 value present
  ** 
  **      Integer : Download ID
  **      String8 : MD5 (if BIT0 set)
  **      
  **   OperationCompleteEvent -  Operation complete (Upload or package dwnld)
  **
  **      Flags   : Bit0 set if MD5 value present
  **                Bit1 set of len is present
  ** 
  **      Integer : uploadID
  **      String8 : MD5 (if BIT0 set)
  **      long    : total length of operation (if BIT1 set)  (V7)
  **
  **   ======================================================================
  */   
   public static DSMPBaseProto operationComplete(byte handle, int id) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_OPERATION_COMPLETE, (byte)0,
                                            handle);
      ret.appendInteger(id);
      return ret;
   }
   public static DSMPBaseProto operationComplete(byte handle, int id, 
                                                 String md5) {
      byte flags = (byte)0;
      boolean domd5 = false;
      if (md5 != null && md5.length() > 0) {
         domd5 = true;
         flags = (byte)0x01;
      }
      
      DSMPBaseProto ret = new DSMPBaseProto(OP_OPERATION_COMPLETE, flags,
                                            handle);
      ret.appendInteger(id);
      
      if (domd5) {
         ret.appendString8(md5);
      }
      
      return ret;
   }
   
   public static DSMPBaseProto operationCompleteEvent(byte handle, int id) {
      DSMPBaseProto ret = new DSMPBaseProto(OP_OPERATION_COMPLETE_EVENT,
                                            (byte)0, handle);
      ret.appendInteger(id);
      return ret;
   }
   
   public static DSMPBaseProto operationCompleteEvent(byte handle, int id, 
                                                      String md5) {
                                                      
      byte flags = (byte)0;
      boolean domd5 = false;
      if (md5 != null && md5.length() > 0) {
         domd5 = true;
         flags = (byte)0x01;
      }
      
      DSMPBaseProto ret = new DSMPBaseProto(OP_OPERATION_COMPLETE_EVENT, flags,
                                            handle);
      ret.appendInteger(id);
      if (domd5) {
         ret.appendString8(md5);
      }
      
      return ret;
   }
   
   public static DSMPBaseProto operationCompleteEvent(byte handle, int id, 
                                                      String md5, long len) {
                                                      
      byte flags = (byte)0x02;
      boolean domd5 = false;
      if (md5 != null && md5.length() > 0) {
         domd5 = true;
         flags |= (byte)0x01;
      }
      
      DSMPBaseProto ret = new DSMPBaseProto(OP_OPERATION_COMPLETE_EVENT, flags,
                                            handle);
      ret.appendInteger(id);
      if (domd5) {
         ret.appendString8(md5);
      }
      
      ret.appendLong(len);
      
      return ret;
   }
}
