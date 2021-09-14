package oem.edge.ed.odc.dropbox.common;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2006 		                         */ 
/*                                                                       */ 
/*     All Rights Reserved					         */ 
/*     US Government Users Restricted Rights			         */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 

/**
 * This interface provides the information regarding what data is to be uploaded
 * for a file. File slots allow uploading of data to a file using multiple concurrent 
 * streams. Each fileslot defines a portion of the file to be uploaded which must
 * be filled in from lower to higher byte, in order (that is, sequentially). Multiple
 * fileslots can be in play at a time, thus allowing multiple upload channels. The 
 * slots are allocated by the dropbox service, and are stamped to belong to a session.
 * This allows multiple clients to cooperate to upload a file even when they are not
 * operating in the same session.
 */
public class FileSlot implements java.io.Serializable {
   long fileid;
   long slotid;
   long sofs;
   long len;
   long intendedlen;
   long sessionid;
   String md5;
   
  /**
   * Instantiate a FileSlot object whie initializing most fields
   * @param fid         File id for slot
   * @param slotid      Slot id for the slot
   * @param len         Current length of the slot
   * @param intendedlen Intended length of the slot
   * @param sofs        Offset into file where this slot begins
   * @param sessionid   Session id owning the slot
   *
   */
   public FileSlot(long fid, long slotid, long len, 
                   long intendedlen, long sofs, 
                   long sessionid) {
      fileid = fid;
      this.slotid = slotid;
      this.len = len;
      this.intendedlen = intendedlen;
      this.sofs = sofs;
      this.sessionid = sessionid;
   }
   
  /**
   * Copy construtor
   * @param fs Source FileSlot to copy 
   */
   public FileSlot(FileSlot fs) {
      fileid = fs.getFileId();
      this.slotid = fs.getSlotId();
      this.len = fs.getLength();
      this.intendedlen = fs.getIntendedLength();
      this.sofs = fs.getStartingOffset();
      this.sessionid = fs.getSessionId();
      this.md5 = fs.md5;
   }
   
  /**
   * Empty/default constructor.
   */
   public FileSlot() {}
   
  /**
   * Get the fileid for which the slot is associated
   * @return fileid for which the slot is associated
   */
   public long   getFileId() { return fileid; }
   
  /**
   * Get the Slot id for the fileslot
   * @return Slot id for the fileslot
   */
   public long   getSlotId() { return slotid; }
   
  /**
   * Get the starting offset into the file for the slot
   * @return starting offset into the file for the slot
   */
   public long   getStartingOffset() { return sofs; }
   
  /**
   * Get the current offset into the file for the slot (startofs+length)
   * @return current offset into the file for the slot (startofs+length)
   */
   public long   getCurrentOffset() { return sofs+len; }
   
  /**
   * Get the bytes left in the file slot (intendedlen -length)
   * @return bytes left in the file slot
   */
   public long   getRemainingBytes() { return intendedlen - len; }
   
  /**
   * Get the current length of the file slot
   * @return current length of the file slot
   */
   public long   getLength() { return len; }
   
  /**
   * Get the intended length of the file slot
   * @return intended length of the file slot
   */
   public long   getIntendedLength() { return intendedlen; }
   
  /**
   * Get the sessionid associated with (owning) the fileslot
   * @return sessionid associated with (owning) the fileslot
   */
   public long   getSessionId() { return sessionid; }
   
  /**
   * Get the MD5 value for the fileslot. 
   * @return MD5 value for the fileslot. 
   */
   public String getMD5() { return md5; }
   
  /**
   * Set the file id for the file slot
   * @param v file id for the file slot
   */
   public void setFileId(long v) {fileid=v;}
   
  /**
   * Set the file id for the file slot
   * @param v file id for the file slot
   */
   public void setSlotId(long v) {slotid=v;}
   
  /**
   * Set the starting offset value for the fileslot
   * @param v starting offset of the fileslot
   */
   public void setStartingOffset(long v) {sofs=v;}
   
  /**
   * Set the current length of the file slot
   * @param v current length of the file slot
   */
   public void setLength(long v) {len=v;}
   
  /**
   * Set the intended length for the file slot
   * @param v intended length for the file slot
   */
   public void setIntendedLength(long v) {intendedlen=v;}
   
  /**
   * Set the sessionid owning the file slot
   * @param v sessionid owning the file slot
   */
   public void setSessionId(long v) {sessionid=v;}
   
  /**
   * Set the MD5 value for the data in the fileslot
   * @param v MD5 value for the data in the fileslot
   */
   public void setMD5(String v) {md5=v;}
   
   public String toString() {
      return "------- FileSlot -------" +
         "\nfileid      = " + fileid + 
         "\nslotid      = " + slotid + 
         "\nstartofs    = " + sofs     + 
         "\nlength      = " + len       + 
         "\nintendedLen = " + intendedlen +
         "\nsessionid   = " + sessionid +
         "\nmd5         = " + md5;
   }
}
