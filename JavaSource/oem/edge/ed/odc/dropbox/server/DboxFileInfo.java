package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.util.*;
import  oem.edge.ed.odc.tunnel.common.DebugPrint;
import org.apache.log4j.Logger;

import java.lang.*;
import java.io.*;
import java.nio.channels.*;
import java.util.*;
import java.util.zip.*;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2003-2006                                    */ 
/*                                                                       */ 
/*     All Rights Reserved                                               */ 
/*     US Government Users Restricted Rights                             */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 

public abstract class DboxFileInfo extends FileInfo {

  // Number of simultaneous active slots. Static as its shared for ALL
   static int        max_num_active_slots    = 1;              //  no multi channel upload
   static long       max_file_slot_size      = 1024*1024*2;    //  2 Meg max slot size
   static long       min_component_size      = 1024*1024*1;    //  1 Meg min comp size
   static boolean    slots_equal_components  = false;
   static boolean    variable_component_size = true;

   long poolid   = -1;
   int  xferrate = 0;
   
  // A file is created in a storage pool
   public long getPoolId()       { return poolid; }
   public void setPoolId(long v) { poolid = v;    }
   
  // A file is created in a storage pool
   public int getFileXferrate()       { return xferrate; }
   public void setFileXferrate(int r) { xferrate = r;    }
   
   static Logger log = Logger.getLogger(DboxFileInfo.class);
   
   protected long intendedSize = -1;
   protected MessageDigestI md5digest = null;
   
   protected FileManager fileManager = null;
   
   public DboxFileInfo(FileManager fm) { fileManager = fm; }
   public DboxFileInfo(FileManager fm, String n, long sz, byte stat, long isz) { 
      super(n, sz, stat);
      fileManager = fm;
      intendedSize = isz;
   }
   public DboxFileInfo(DboxFileInfo i) { 
      super(i);
      fileManager  = i.getFileManager();
      intendedSize = i.getFileIntendedSize();
      setPoolId(i.getPoolId());
   }
   
   public FileManager getFileManager() { return fileManager; }
   
   public void setFileIntendedSize(long sz) throws DboxException {
      intendedSize = sz;
   }
   
   public void setServersideFileStatus(byte stat) throws DboxException {
      setFileStatus(stat);
   }
      
   public void forceSetFileStatus(byte stat) throws DboxException {
      setServersideFileStatus(stat);
   }
      
   public void forceSetFileXferrate(int xferrate) throws DboxException {
      this.xferrate = xferrate;
   }
      
   public void setFileMD5ObjectFromBytes(byte[] md5bytes) throws DboxException {
      DropboxFileMD5 md5obj = null;
      
      if (md5bytes != null) {
         md5obj = new DropboxFileMD5();
         try {
            md5obj.stateFromBytes(md5bytes, 0, md5bytes.length);
         } catch(Exception e) {
            throw new DboxException("Error decoding MD5 state from bytes", e);
         }
      }
      setFileMD5Object(md5obj);
   }
      
   public void forceSetFileMD5(String v) throws DboxException {
      setFileMD5(v);
   }
      
   public void forceSetFileSize(long ss) throws DboxException {
      setFileSize(ss);
   }
      
   public void forceSetFileMD5Object(MessageDigestI v) throws DboxException {
      setFileMD5Object(v);
   }
   
   public void forceSetSizeAndMD5Object(long sz, 
                                        MessageDigestI v) throws DboxException {
      forceSetFileMD5Object(v);
      forceSetFileSize(sz);
   }   
      
   public void setFileMD5Object(MessageDigestI v) throws DboxException {
      if (v != null) {
         setFileMD5(v.hashAsString());
         md5digest = new DropboxFileMD5(v.getMD5State());
      } else {
         setFileMD5("");
         md5digest = null;
      }
   }
      
   public MessageDigestI getFileMD5Object() {
      return md5digest;
   }
      
   public abstract Vector getComponents() throws DboxException ;
   
   public DboxFileComponent getComponentContainingOffset(long ofs)
      throws DboxException {
      
      Vector v = getComponents();
      Iterator it = v.iterator();
      while(it.hasNext()) {
         DboxFileComponent fc = (DboxFileComponent)it.next();
         long sofs = fc.getStartingOffset();
         long eofs = sofs + fc.getIntendedFileSize();
         if (sofs >= ofs && ofs < eofs) {
            return fc;
         }
      }
      return null;
   }
      
   public abstract Vector getFileSlots() throws DboxException;
   public abstract DboxFileSlot getFileSlot(long slotid) throws DboxException;
   public abstract void removeFileSlot(User user, long slotid) throws DboxException;
   public abstract void releaseFileSlot(User user, long slotid) throws DboxException;
   public abstract void truncate(long filelen)  throws DboxException;
   public abstract DboxFileSlot allocateFileSlot(User user)  throws DboxException;
   public abstract void cullSlots() throws DboxException;
   
   public Vector getMD5Components(long inlen) throws DboxException {
      log.debug("DboxFileInfo: Fix getMD5Components to do the right thing!");
      return getComponents();
   }
   
   public void deleteComponent(long compid)  throws DboxException {
   
      DboxFileComponent comp = getComponent(compid);
      
      if (comp != null) {
         comp.deleteFile();
            
         long csofs = comp.getStartingOffset();
         long ceofs = csofs + comp.getFileIntendedSize();
         Vector slots = getFileSlots();
         int vjsz = slots.size();
         for(int j=0; j < vjsz; j++) {
            DboxFileSlot slot = (DboxFileSlot)slots.elementAt(j);
            long ssofs = slot.getStartingOffset();
            if (ssofs >= csofs && ssofs  <= ceofs) {
               slots.removeElementAt(j);
               j--;
            }
         }
         
        // If this component was included in slot0, then negate that info
         if (csofs < filesize) {
            forceSetFileMD5("");
            filesize = csofs;
         }
         
         return;
      }
      
      throw new DboxException("Specified component to delete not found: fileid = " + 
                              getFileId() + " compid = " + compid);
   }
   
   public void deleteComponents()  throws DboxException {
   
      Vector components = getComponents();
      Enumeration enum = components.elements();
      while(enum.hasMoreElements()) {
         DboxFileComponent comp = (DboxFileComponent)enum.nextElement();
         filesize -= comp.getFileSize();
         comp.deleteFile();
      }
      components.removeAllElements();
      Vector slots = getFileSlots();
      slots.removeAllElements();
      forceSetFileMD5("");
      filesize = 0;
   }
      
   public abstract void recalculateFileSize() throws DboxException;
      
   public long getFileIntendedSize() { return intendedSize; }
      
   public abstract DboxFileComponent getComponent(long idx) 
      throws DboxException;
      
      
  //Changed Methods
   public String calculateMD5(long inlen) throws Exception {
      
      try {
	   	
         MessageDigestI digest = calculateAndReturnMD5(inlen);
         return digest.hashAsString();
		  
      } catch(java.security.NoSuchAlgorithmException ee) {
         throw new IOException("No MD5 Algo found");
      } catch(DboxException dbe) {
         throw new IOException("Dbox specific error : " + DboxAlert.getStackTrace(dbe));
      }
   }
      
   public MessageDigestI calculateAndReturnMD5(long inlen) 
      throws Exception {
      return calculateAndReturnMD5(inlen, false);
   }
   
  // This routine will return the MD5 object for this file for the specified len.
  //  If the len is specified as > filesize, then exception will be thrown.
  //  If setMD5 is true, then any component MD5 and FileInfo MD5 values will
  //  be forceSet and component filesizes increased as appropriate. The comp
  //  filesize can only be increased if the compIntendedSize will allow.
   public MessageDigestI calculateAndReturnMD5(long inlen, boolean setMD5) 
      throws Exception {
      
      MessageDigestI digest = null;
      
      byte buf[] = new byte[32768];
      
     // Get inlen bytes, unless 0, then get entire file
      long len = inlen == 0 ? getFileSize() : inlen;
      
      if (getFileSize() < len) {
         throw new 
            Exception("MD5 Calculation failed: Filesize is less than the request length");
      }
      
     // If we are to update MD5 info, then lock the file
      if (setMD5) {
         fileManager.lockfile(getFileId());
      }
      
      
     // If we are asking for MD5 for entire file, return the FILEs MD5 (if set)
     //
     // Note: This routine DOES get used to help calculate the FILE MD5. 
     //       so the incore filesize MAY be inceased and MD5 nulled to force
     //       the calculation AND setting of FILE MD5
     //
      if (getFileSize() == len) {
         try {
            digest = getFileMD5Object();
            if (digest != null) return digest;
            
           // Handle 0 len file case
            if (len == 0) {
               digest = new DropboxFileMD5();
               
               if (setMD5) {
                  forceSetFileMD5Object(digest);
               }
               return digest;
            }
         } catch(DboxException dbex) {
         }
      }
      
      
     // Get all components in the MD5 pathway. This means starting with
     //  the Component which SHOULD have a usable MD5 object thru the
     //  last component containing bytes which are interesting to the 
     //  specified length
     //
     // It MAY have ALL components ... but allows for optimization
      Vector components = getMD5Components(len);
      
      synchronized (this) {
         
         if (len > 0) {
		  	 
            RandomAccessFile compIs = null;
            try {
            
               Enumeration enum = components.elements();
               DboxFileComponent lastGoodComp = null;
               DboxFileComponent reprocess    = null;
               MessageDigestI compMD5 = null;
               
              // 1) We search for a component to start with
              // 2) Once found, we stream in data from the components
              //    and calculate the MD5 value
              // 3) If setMD5 is true, then we save our hard work
              //    along the way.
              // 4) If we did NOT find a valid MD5 and SHOULD have, then
              //    we just let the outside logic take over
              // x) reprocess allows us to loop to reprocess a component
              //    once we figure out where we need to start
               while(reprocess != null || enum.hasMoreElements()) {
					
                  DboxFileComponent comp = reprocess;
                  if (comp == null) comp = (DboxFileComponent)enum.nextElement();
                  
                  reprocess = null;
                  
                  long clen     = comp.getFileSize();					 
                  long ilen     = comp.getIntendedFileSize(); 
                  long sofs     = comp.getStartingOffset();
                  long ieofs    = sofs + ilen;
                  long compslen = sofs + clen;
                  
                  long dofs     = sofs;
                  
                 // Validate the components are sequential (and good)
                  if (lastGoodComp != null) {
                     
                     if (lastGoodComp.getStartingOffset() + 
                         lastGoodComp.getIntendedFileSize() != sofs ||
                         lastGoodComp.getIntendedFileSize() != 
                         lastGoodComp.getFileSize()) {
                        throw new DboxException("Components look odd!\n----LGC----\n" + 
                                                lastGoodComp.toString() +
                                                "\n----comp-----\n" + 
                                                comp.toString());
                     }
                  }
                  
                 // If no compMD5, then we have not started to calculate
                 //  MD5 yet ... still searching for good starting point
                  if (compMD5 == null) {
                  
                    // If this component is a usable one, remember it
                     if (compslen <= len && 
                         comp.getComponentMD5State() != null) {
                        lastGoodComp = comp;
                     }
                  
                    // If this component is exactly right, take it
                     if (compslen == len && lastGoodComp == comp) {
                        compMD5 = comp.getComponentMD5State();
                        if (compMD5 != null) {
                           
                          // If the FileInfo filesize == the asked for len, AND
                          //  we were asked to save MD5's, then do so
                           if (setMD5 && getFileSize() == len) {
                              forceSetFileMD5Object(compMD5);
                           }
                           return compMD5;
                        }
                        
                       // Nope, let the outside code handle it (should not happen)
                        throw new Exception("Component SHOULD have had MD5: " 
                                            + comp.toString());
                     } 
                     
                    // If we don't have a lastGoodComp and the component we are 
                    //  working on is NOT at ofs 0, DITCH ... let outside
                    //  code handle it
                     if (lastGoodComp == null && sofs > 0) {
                        throw new Exception("Component to start calc from has no MD5: " 
                                            + comp.toString());
                     }
                  
                     
                    // If this component was NOT considered GOOD or if this component
                    //  contains the last byte in question ... got to start
                     if (lastGoodComp != comp || len <= ieofs) {
                         
                       // setup to do the bizness
                        if (lastGoodComp != null) {
                        
                          // If we have a lastGoodComp to start with, use it
                     
                           compMD5 = lastGoodComp.getComponentMD5State(); 
                           if (compMD5 == null) {
                              throw new Exception("Error getting MD5State");
                           }
                           
                          // If current comp is NOT the lastGood, set up to reprocess it
                           if (lastGoodComp != comp && 
                               lastGoodComp.getFileSize() < 
                               lastGoodComp.getIntendedFileSize()) {
                              reprocess = comp;
                              comp      = lastGoodComp;
                           
                             // Get variables reset for LAST
                              clen     = comp.getFileSize(); 
                              ilen     = comp.getIntendedFileSize(); 
                              sofs     = comp.getStartingOffset();
                              ieofs    = sofs + ilen;
                              compslen = sofs + clen;
                              
                             // Start buffer read from end of current component
                              dofs = compslen;  
                           } else if (lastGoodComp == comp) {
                             // Start buffer read from end of current component
                              dofs = compslen;  
                           } else {
                              dofs = sofs;  
                           }
                           
                           log.debug("Starting with dofs=" + dofs + " comp: \n" + comp.toString());
                        } else {
                        
                          // This must be the first component (ofs 0)
                           compMD5 = new DropboxFileMD5();
                           log.debug("Starting new MD5"); 
                        }
                     }
                  }
                     
                 // If we have work to do
                  if (compMD5 != null) {
                     
                    // Read all bytes needed from component
                     long noBytestoRead = len-dofs;
                     if (noBytestoRead+dofs > ieofs) {
                        noBytestoRead = ieofs-dofs;
                     }
                     
                     if (noBytestoRead > 0) {
                        compIs = comp.getDownloader(dofs);
                        
                       // Lock shared 
                        if (DboxFileSlot.do_record_locking) {
                           compIs.getChannel().lock(compIs.getFilePointer(), 
                                                    noBytestoRead, true);
                        }
                     }
                     
                     long eofs = dofs;
                     
                    //System.out.println("Reading " + noBytestoRead + " and ofs " + dofs + " from \n" + comp.toString());
                     
                     while(noBytestoRead > 0) {
                        int toread = (int)((noBytestoRead > buf.length) 
                                           ? buf.length
                                           : noBytestoRead);
                                           
                       //System.out.println(" doing read = " + toread);
                        int r = compIs.read(buf, 0, toread);
                       //System.out.println(" got r = " + r);
                        if (r < 0) throw new Exception("Ran out of bytes while reading component!");
                        if (r > 0) compMD5.update(buf,0,r);
                        noBytestoRead -= r;
                        eofs += r;
                     }
                     
                     if (compIs != null) {
                        compIs.close();
                        compIs = null;
                     }
                     
                    // If we are saving our hard work to the meta data ... doit
                     if (setMD5) {
                     
                       // If the component should be resized and/or MD5'd, doit
                        if (eofs > compslen) {
                        
                           comp.forceSetSizeAndMD5Object(eofs-sofs, compMD5);
                           
                        } else if (eofs == compslen) {
                           comp.forceSetFileMD5Object(compMD5);
                        }
                     
                       // If the MD5 object is good for the FileInfo ... saveit
                        if (eofs == getFileSize()) {
                           forceSetFileMD5Object(compMD5);
                        }
                     }
                     
                     if (eofs == len) {
                        return compMD5;
                     }
                     
                    // Allow component goodness checking at top of loop for next pass
                     lastGoodComp = comp;
                  }
               }
               
               throw new Exception("Huh? We are done with the components, yet not to the end?");
	        
            } catch(Exception ex) {
               DebugPrint.println(DebugPrint.WARN, "Error calculating MD5 value: ");
               DebugPrint.println(DebugPrint.WARN, this.toString());
               DebugPrint.println(DebugPrint.WARN, "----- Exception -----");
               DebugPrint.println(DebugPrint.WARN, ex);
            } finally {
               try {
                  if (compIs != null) compIs.close();
               } catch(Exception eee) {}
            }
         }
         
        //
        // We get here only if above short cut method fails in some way.
        //
         
         RandomAccessFile fis = null;
         
         try {
            digest = new DropboxFileMD5();
            
            boolean grew = false;
         
            Enumeration enum = getComponents().elements();
            long ofs = 0;
            while(enum.hasMoreElements() && ofs < len) {
               DboxFileComponent comp = (DboxFileComponent)enum.nextElement();
               
               long csofs = comp.getStartingOffset();
               
               if (ofs != csofs) {
                  log.info("ofs = " + ofs + " len = " + len + " csofs = " + 
                           csofs + " comp: \n" + comp.toString());
                  throw new IOException("Component does NOT have correct starting offset");
               }
               
               long cilen   = comp.getIntendedFileSize();
               long clen    = comp.getFileSize();
               long cieofs  = csofs + cilen;
               long ceofs   = csofs + clen;
               
               long left    = len - csofs;
               
              // If bytes left to write is more than comp has, give just cilen
               if (left > cilen) {
                  left = cilen;
               }
               
              // If we are already growing components, then this last component
              //  should have a 0 file size
               if (grew && clen > 0) {
                  throw new DboxException("Already grew previous component and clen NOT 0!");
               }
               
               fis = comp.getDownloader(ofs);
               
              // Lock shared 
               if (DboxFileSlot.do_record_locking) {
                  fis.getChannel().lock(fis.getFilePointer(), left, true);
               }
               
               
              // Read all appropriate data and MD5 it
               while(left > 0) {
               
                  int r = (int)(left > buf.length ? buf.length : left);
                  
                  r = fis.read(buf, 0, r);
                  if (r == -1) break;
                  
                  digest.update(buf, 0, r);
                  
                  left -= r;
                  ofs  += r;
               }
               
              // If we are into component grow mode, mark it so
               if (ofs > ceofs) {
                  grew = true;
               }
               
              // If we are saving our hard work to the meta data ... doit
               if (left == 0 && setMD5) {
                  
                 // If the component should be resized and/or MD5'd, doit
                  if (ofs > ceofs) {
                     
                     comp.forceSetSizeAndMD5Object(ofs-csofs, digest);
                     
                  } else if (ofs == ceofs) {
                     comp.forceSetFileMD5Object(digest);
                  }                  
               }
               
               fis.close();
               fis = null;
            }
            
            if (ofs != len) {
               throw new IOException("Ran out of bytes before finished MD5");
            }
            
           // If the MD5 object is good for the FileInfo ... saveit
            if (setMD5 && ofs == getFileSize()) {
               forceSetFileMD5Object(digest);
            }
            
         } finally {
            try { if (fis != null) fis.close(); } catch(Exception fisce) {}
         }
      }
      return digest;
   }
      
   public abstract DboxFileComponent createComponent() throws DboxException;
      
   public String toString() {
      String ret = super.toString() + 
         "\n -- DboxFileInfo --" +
         Nester.nest("\nintendedSize   " + intendedSize +
                     "\npoolid         " + poolid + 
                     "\nxferrrate      " + xferrate + 
                     "\n----> Components <----");
                     
      try {
         Vector components = getComponents();
         Enumeration enum = components.elements();
         while(enum.hasMoreElements()) {
            Object o = enum.nextElement();
            ret += Nester.nest("\n" + o.toString(), 2);
         }
      } catch(DboxException ee) {}
      return ret;
   }
}
