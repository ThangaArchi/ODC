package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.util.*;

import java.lang.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2006                                          */
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

public abstract class DboxFileSlot extends FileSlot {

   FileManager fileManager = null;
   
   protected static Logger log = Logger.getLogger(DboxFileSlot.class.getName());
   
  // Controls whether getting downloader will lock the file with channel
  // Static, so controls for ALL users/uses.
   protected static boolean do_record_locking = false;
   
   long  starttime = new java.util.Date().getTime();
   
   long locktime;
   long componentid;
   
   DropboxFileMD5 md5obj = null;
      
   public DboxFileSlot() {
      super();
   }
      
   public DboxFileSlot(DboxFileSlot fs) {
      super(fs);
      
      this.componentid = fs.getComponentId();
      this.locktime = fs.getLockTime();
      this.fileManager = fs.fileManager;
      this.md5obj = fs.md5obj;
   }
      
   public DboxFileSlot(FileManager fm, 
                       long fid,
                       long slotid,
                       long size, 
                       long intendedSize,
                       long startofs, 
                       long sessionid,
                       long compid,
                       long locktime) {
      super(fid, slotid, size, intendedSize, startofs, sessionid);
      
      this.componentid = compid;
      this.locktime = locktime;
      this.fileManager = fm;
   }
   
   public abstract java.io.RandomAccessFile getUploader() throws DboxException;
   
   public long getLockTime() { return locktime; }
   public void setLockTime(long t) { locktime = t; }
   
  /**
   * Force sets the length of the slot, and auto releases it if its full
   */
   public boolean forceSetLength(long t) throws DboxException {
      setLength(t);
      boolean ret = getLength() >= getIntendedLength();
      if (ret) { setSessionId(0); }
      return ret;
   }
   
   public MessageDigestI getMD5Object() { 
      return (md5obj!= null)?new DropboxFileMD5(md5obj.getMD5State()):null; 
   }
   
   public void setMD5ObjectFromBytes(byte[] buf) throws Exception {
      setMD5ObjectFromBytes(buf, 0, buf.length);
   }
   
   public void setMD5ObjectFromBytes(byte[] buf, int ofs, int len) throws Exception {
      this.md5obj = new DropboxFileMD5();
      md5obj.stateFromBytes(buf, ofs, len);
      setMD5(md5obj.hashAsString());
   }
   
   public void setMD5Object(MessageDigestI md5State){
      if (md5State == null) {
         this.md5obj = null;
         setMD5(null);
      } else {
         this.md5obj = new DropboxFileMD5(md5State.getMD5State());
         try {
            setMD5(md5obj.hashAsString());
         } catch(Exception e) {
         }
      }
   }

  /**
   * Force sets the length/md5object of the slot, and auto releases it if its full
   */
   public void setLengthAndMD5Object(long sz, 
                                     MessageDigestI v) throws DboxException {
      setLength(sz);
      setMD5Object(v);
   }   
   
   public void forceSetMD5Object(MessageDigestI v) throws DboxException {
      setMD5Object(v);
   }
   
   public boolean forceSetLengthAndMD5Object(long sz, 
                                             MessageDigestI v) throws DboxException {
      forceSetLength(sz);
      forceSetMD5Object(v);
      boolean ret = getLength() >= getIntendedLength();
      if (ret) { setSessionId(0); }
      return ret;
   }   
   
   public long getComponentId() { return componentid; }
   public void setComponentId(long t) { componentid = t; }
   
   public long getStartTime() { return starttime; }
   public void setStartTime(long t) { starttime = t; }
      
   public String toString() {
      return Nester.nest("------ DboxFileSlot -------" +
                         "\nlocktime    = " + ((new Date(locktime)).toString()) +
                         "\ncomponentid = " + componentid + 
                         "\n" + super.toString());
   }
}
