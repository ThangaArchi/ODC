package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.util.*;

import java.lang.*;
import java.lang.reflect.Method;
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
/*     (C)Copyright IBM Corp. 2003-2004,2005,2006                           */
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

public class DboxFileComponent {

   FileManager fileManager = null;
   
   protected static Logger log = Logger.getLogger(DboxFileComponent.class.getName());
   
   long  starttime = new java.util.Date().getTime();
   long   size             = 0;
   String filename         = null;
   String parentDir        = null;
   long   fileid           = 0;
   long   intendedSize     = 0;
   long   startingOffset   = 0;
   byte[] md5blob = null;
   MessageDigestI compMD5=null;

   
   boolean threadedclosewillhelp = false;
   
   public DboxFileComponent(FileManager fm, long fid,
                            long size, long intendedSize,
                            long startingOfs) {
      fileManager = fm;
      fileid = fid;
      this.size = size;
      this.intendedSize = intendedSize;
      this.startingOffset = startingOfs;
   }
   
   public DboxFileComponent(FileManager fm, long fid,
                            long size, long intendedSize,
                            long startingOfs, String fullpath) {
      fileManager = fm;
      fileid = fid;
      this.size = size;
      this.intendedSize = intendedSize;
      this.startingOffset = startingOfs;
      setFullPath(fullpath);
   }
      
   public long getFileId() { return fileid; }
   
   public long getStartingOffset() { return startingOffset; }
   public void setStartingOffset(long o) { startingOffset = o; }
   
   public long getComponentId() { return getStartingOffset(); }
   
   public long getStartTime() { return starttime; }
      
   public boolean threadedCloseWillHelp() { return threadedclosewillhelp; }
   public void    threadedCloseWillHelp(boolean v ) {threadedclosewillhelp=v;}
   
   public void setFullPath(String p) { 
      int idx = p.lastIndexOf(File.separator);
      
      filename = p;
      if (idx == 0) {
         parentDir = "/";
      } else if (idx >= 0) {
         parentDir = p.substring(0, idx);
      } else {
         parentDir = ".";
      }
   }
   
   public void setFileSize(long sz)         { size = sz;           }
   public void setFileIntendedSize(long sz) { intendedSize = sz;   }
   
   public void setMD5blobBytes(byte[] buf){
	this.md5blob = buf;
   }
   public void setFileMD5Object(MessageDigestI md5State){
      this.compMD5 = md5State;
   }
   public void setCompMD5State(MessageDigestI md5State){
      this.compMD5 = md5State;
   }   

   public void forceSetIntendedSize(long ss) throws DboxException {
      setFileIntendedSize(ss);
   }
   
   public void forceSetFileSize(long ss) throws DboxException {
      setFileSize(ss);
   }
      
   public void forceSetFileMD5Object(MessageDigestI v) throws DboxException {
      if (v != null) {
         this.compMD5 = new DropboxFileMD5(v.getMD5State());
      } else {
         this.compMD5 = null;
      }
   }
   
   public void forceSetSizeAndMD5Object(long sz, 
                                        MessageDigestI v) throws DboxException {
      forceSetFileMD5Object(v);
      forceSetFileSize(sz);
   }   
   
   
   public long getFileIntendedSize()        { return intendedSize; }
   public long getFileSize()                { return size;         }
   public long getIntendedFileSize()        { return intendedSize; }
   
   public String getFullPath() { return filename; }
      
   public String toString() {
      return "DboxFileComponent :" +
         Nester.nest("\nsize        = " + size + 
                     "\nfilename    = " + filename + 
                     "\nparentDir   = " + parentDir + 
                     "\nfileid      = " + fileid + 
                     "\nintendedSiz = " + intendedSize + 
                     "\nstartingOfs = " + startingOffset);
   }
      
   public long getSizeDelta() { 
      return intendedSize - size;
   }
      
   public void deleteFile() throws DboxException {
      File f = new File(filename);
         
      if (f.exists() && !f.delete()) {
         throw new DboxException("Error deleting component " + filename, 0);
      }
      fileManager.getFileAllocator().returnSpace(parentDir, intendedSize);
   }
      
   public void returnSpace() throws DboxException {
      fileManager.getFileAllocator().returnSpace(parentDir, intendedSize);
   }
      
   public void truncateFile(long len) throws DboxException {
      File f = new File(filename);
         
      if (len > intendedSize) {
         throw new DboxException("Error truncating component " + filename + 
                                 ". Cannot make component LARGER");
      }      
      
      if (f.exists()) {
         try {
         
            if (len < size) {
               RandomAccessFile rf = new RandomAccessFile(filename, "rw");
               rf.getChannel().truncate(len);
               rf.close();
               forceSetFileSize(len);
            }
            
         } catch(Exception e) {
            throw new DboxException("Error truncating component " + filename, e);
         }
      }
      
      if (len < intendedSize) {
         fileManager.getFileAllocator().returnSpace(parentDir, intendedSize - len);
         forceSetIntendedSize(len);
      }
   }
      
   public void incrementSize(int sz) {
      size += sz;
   }
      
   public void acquireSpace(long poolid) throws DboxException {
      SpaceAllocation sa = 
         fileManager.getFileAllocator().allocateSpace(intendedSize, poolid);
      intendedSize = sa.getSize();
      parentDir = sa.getDirectory();
      filename = parentDir + File.separator + fileid + "." + getStartingOffset();
   }
   
   public java.io.RandomAccessFile getDownloader() 
      throws DboxException, IOException {
      return getDownloader(0);
   }
   
   public java.io.RandomAccessFile getDownloader(long startofs) 
      throws DboxException, IOException {
      if (startofs < startingOffset || 
          startofs > startingOffset + intendedSize) {
         throw new DboxException("Downloader would be outside the bounds of comp");
      }
      String fname = getFullPath();
      RandomAccessFile rf = new RandomAccessFile(fname, "r");
      log.debug("Seeking in " + filename + " ofs = " + (startofs-startingOffset));
      rf.seek(startofs-startingOffset);
      return rf;
   }
   
   
   public OutputStream makeOutputStream() throws IOException {
         
      return new BufferedOutputStream(new FileOutputStream(filename, 
                                                           size != 0));
   }
      
   public InputStream makeInputStream() throws IOException {
         
      if (filename == null) {
         throw new IOException("File is null");
      }
      return new FileInputStream(filename);
   }
      
   public void complete() throws DboxException {
      if (size < intendedSize) {
        // Don't complain if we couldn't return space. User does not care
        //  and it will have generated an Alert
         try {
            fileManager.getFileAllocator().returnSpace(parentDir, 
                                                       intendedSize - size);
         } catch(DboxException dd) {}
         setFileIntendedSize(size);
      }
   } 
   
   public MessageDigestI getComponentMD5State() throws Exception{	   		
      return getFileMD5Object();
   }
   public MessageDigestI getFileMD5Object() throws Exception{	   		
      
      try {
         if (compMD5 == null && this.md5blob != null) {
            this.compMD5 = new DropboxFileMD5();
            this.compMD5.stateFromBytes(this.md5blob, 0, this.md5blob.length);
         } 
      } catch (IOException e) {
         e.printStackTrace(System.out);
         throw new IOException("Blob is empty for this component");
      } catch (ClassNotFoundException e) {
         e.printStackTrace(System.out);
         throw new ClassNotFoundException("DropboxFileMD5 class not found");
      }
      return compMD5; 
   }
}
