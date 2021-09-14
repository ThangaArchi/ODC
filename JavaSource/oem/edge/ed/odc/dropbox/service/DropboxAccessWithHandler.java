package oem.edge.ed.odc.dropbox.service;


import java.util.Vector;
import java.util.HashMap;

import oem.edge.ed.odc.dsmp.common.DboxException;
import oem.edge.ed.odc.dropbox.common.FileSlot;

import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.activation.DataHandler;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2006                                         */ 
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


public interface DropboxAccessWithHandler extends DropboxAccess {
  /**
   * Obtains a FileSlot to be used for file upload to the package/file in question.
   * Its up to the server's discretion as to the size/location of the data. The 
   * totalthreads value along with the totalIntendedSize for the file upload will
   * be used as hints to determine an appropriate slot size.
   *
   * @param  packid        The identifier of the package for upload
   * @param  fileid        The identifier of the file    for upload
   * @param  slotid        The slotid (FileSlot->getSlotId()) for upload
   * @param  is            The InputStream from where the data will be obtained for
   *                       upload
   * @param  getNextSlot   If true, the next appropriate FileSlot for upload will be
   *                       returned. Otherwise, a null is returned.
   * @return FileSlot      Describes the location and size for next upload slot
   * @throws               DboxException describes failure of routine
   *
   */
   public FileSlot uploadFileSlotToPackageWithHandler(long packid, 
                                                      long fileid,
                                                      long slotid,
                                                      boolean getNextSlot,
                                                      DataHandler h)
      throws DboxException, RemoteException;
   
  /**
   * Downloads the specified package using the provided encoding type. This download
   *  method is NOT restartable, nor is it threadable (sequential only). The returned
   *  input stream should be used to sequentially access all the data in the package,
   *  which will be encoded as specified.  Supported encodings are:
   *<p>
   *&nbsp;&nbsp;&nbsp;&nbsp;tar  -  Tar file containing all files in package
   *&nbsp;&nbsp;&nbsp;&nbsp;tgz  -  Tar file containing all files in package, and gziped
   *&nbsp;&nbsp;&nbsp;&nbsp;zip  -  Zip file containing all files in package
   *<p>  
   * NOTE: The end size of the package is not known while the package is being 
   *  streamed to the caller. If this API is being accessed using a transport which
   *  has a max size per invocation (such as 2GIG for http), then its possible that
   *  the data will not be fully delivered.
   *
   * @param  packid        The identifier of the package for download
   * @param  encoding      The type of encoding to apply to files
   * @return InputStream   Used to read downloaded bytes
   * @throws               DboxException describes failure of routine
   */
   public DataHandler downloadPackageWithHandler(long packid, 
                                                 String encoding)
      throws DboxException, RemoteException;
   
  /**
   * Downloads the specified file from the specified package. This download
   *  method is NOT restartable, nor is it threadable (sequential only).
   *<p>  
   * NOTE:  If this API is being accessed using a transport which
   *  has a max size per invocation (such as 2GIG for http), then its possible that
   *  the data will not be fully delivered.  
   *
   * @param  packid        The identifier of the package containing file for download
   * @param  fileid        The identifier of the file for download
   * @return InputStream   Used to read downloaded bytes
   * @throws               DboxException describes failure of routine
   *
   */
   public DataHandler downloadPackageItemWithHandler(long packid, 
                                                     long fileid)
      throws DboxException, RemoteException;
                                     
  /**
   * Downloads a section of the specified file from the specified package. The file
   *  download process is mostly driven by the client. The one major caveat is that
   *  the final 10k of data in a file must be downloaded in a single invocation (can
   *  be more than 10k, but must be at least 10k, or entire file).  This caveat exists
   *  to help ensure non-repudiation for downloads. When a user of the API asks for,
   *  and is delivered, from the Servers standpoint, the final 10k of a file, then
   *  the the file access record for that user is marked completed.
   *
   * It is the server's perogative to determine how much data to return for the 
   *  invocation, but it will NOT be greater than len bytes (could be <= len, however).
   *
   * @param  packid        The identifier of the package containing file for download
   * @param  fileid        The identifier of the file for download
   * @param  ofs           Byte offset into the file to begin the data transfer
   * @param  len           Max number of bytes to download
   * @return byte[]        bytes being downloaded
   * @throws               DboxException describes failure of routine
   *
   */
   public DataHandler downloadPackageItemWithHandler(long packid, 
                                                     long fileid,
                                                     long ofs,
                                                     long len)
      throws DboxException, RemoteException;
}
