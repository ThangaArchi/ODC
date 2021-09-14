package oem.edge.ed.odc.dropbox.common;

import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.util.*;

import java.lang.*;
import java.util.*;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2002-2005,2006		                 */ 
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
 * Bean providing information about the a package based File item.
 */
public class FileInfo implements java.io.Serializable {
   protected byte   filestatus = DropboxGenerator.STATUS_INCOMPLETE;
   protected long   expiration = 0;
   protected long   creation   = 0;
   protected long   filesize   = 0;
   protected String filename   = "";
   protected long   fileid     = -1;
   protected String filemd5    = "";
   
  /**
   * Empty/default constructor.
   */
   public FileInfo() {}
   
  /**
   * Copy construtor
   * @param i Source FileInfo to copy 
   */
   public FileInfo(FileInfo i) {
      filestatus = i.getFileStatus();
      expiration = i.getFileExpiration();
      creation   = i.getFileExpiration();
      filesize   = i.getFileSize();
      filename   = i.getFileName();
      fileid     = i.getFileId();
      filemd5    = i.getFileMD5();
   }
   
  /**
   * Create a FileInfo with specified name, size and status
   * @param file File name to use 
   * @param size Size of the file
   * @param stat Status of the file
   */
   public FileInfo(String file, long size, byte stat) {
      filestatus = stat;
      expiration = 0;
      creation   = 0;
      filesize   = size;
      filename   = file;
      fileid     = 0;
   }
   
  /**
   * Set the File status
   * <ul>
   *   <li>STATUS_NONE - File is not complete, with no errors (uploading or empty)</li>
   *   <li>STATUS_INCOMPLETE - File was uploaded, resulting in error</li>
   *   <li>STATUS_COMPLETE - File is completely uploaded</li>
   * </ul>
   * @param v new File status value
   */
   public void setFileStatus(byte v)     { filestatus = v;     }
  /**
   * Set the Milliseconds since 70 GMT for the file expiration
   * @param v new File expiration value
   */
   public void setFileExpiration(long v) { expiration = v;     }
  /**
   * Set the Milliseconds since 70 GMT that the file object was created
   * 
   * @param v new File creation time value
   */
   public void setFileCreation(long v)   { creation = v;       }
  /**
   * Set the size of the file
   * @param v new File size value
   */
   public void setFileSize(long v)       { filesize   = v;     }
  /**
   * Set the name of the file in the dropbox
   * @param v new File name value
   */
   public void setFileName(String v)     { filename   = v;     }
  /**
   * Set the fileid for the file
   * @param v new File id value
   */
   public void setFileId(long v)         { fileid     = v;     }
  /**
   * Set the file md5 associated with the file
   * @param v new File md5 value
   */
   public void setFileMD5(String v)      { filemd5 = v;        }
   
  /**
   * Get the status of the file
   * @see #setFileStatus
   * @return byte status of the file
   */
   public byte   getFileStatus()         { return filestatus;  }
  /**
   * Get the milliseconds since 70 GMT that the file will/did expire
   * @return long expiration time of the file
   */
   public long   getFileExpiration()     { return expiration;  }
  /**
   * Get the milliseconds since 70 GMT that the file was created
   * @return long creation time of the file
   */
   public long   getFileCreation()       { return creation;    }
  /**
   * Get the size of the file
   * @return long size of the file
   */
   public long   getFileSize()           { return filesize;    }
  /**
   * Get the name of the file
   * @return String name of the file
   */
   public String getFileName()           { return filename;    }
  /**
   * Get the MD5 value of the file 
   * @return String MD5 value
   */
   public String getFileMD5()            { return filemd5;     }
  /**
   * Get the unique file id for the file
   * @return long fileid for the file
   */
   public long   getFileId()             { return fileid;      }
   
   public String toString() {
      return "FileInfo" +
         Nester.nest("\nfilename   = " + filename   +
                     "\nexpiration = " + expiration +
                     "\ncreation   = " + creation   +
                     "\nfilesize   = " + filesize   +
                     "\nfileid     = " + fileid     +
                     "\nfilestatus = " + filestatus +
                     "\nfilemd5    = " + filemd5);
   }
   
   public int hashCode() {
      return (int)fileid;
   }
   
  /**
   * Compare objects to test for a match. 
   * Compare fileid and file name are used to determine a match.
   */
   public boolean equals(Object o) {
      if (o instanceof FileInfo) {
         FileInfo to = (FileInfo)o;
         if (fileid == to.fileid) {
            if (filename != null && to.filename != null) {
               if (!filename.equals(to.filename)) return false;
            } else if (filename != to.filename)   return false;
            
           // Thats close enough for me
            return true;
         } 
      } 
      return false;
   }
   
}
