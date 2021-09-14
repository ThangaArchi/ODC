package oem.edge.ed.odc.webdropbox.server;

import org.apache.struts.upload.*;
import org.apache.struts.action.*;
import org.apache.commons.fileupload.*;
import java.io.*;
import java.util.*;
import oem.edge.ed.odc.dropbox.common.*;
import oem.edge.ed.odc.dropbox.client.sftpDropbox;
import oem.edge.ed.odc.dropbox.service.helper.*;

import javax.servlet.http.*;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2005-2006                                    */ 
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

/**
 * The fileitem is the wrapper used by the FileUpload object to accomplish
 *  the upload. The default version is the DiskFileItem, which stores the
 *  file temporarily on disk ... not what we want. So, this version will
 *  return an OutputStream to the caller, which will jam the data directly
 *  into the dropbox. When close is called on said OutputStream, it will do
 *  the normal operation close we would do to the sftpDropbox operation.
 *
 * For all errors relating to the dropbox transfer, we can choose to either
 *  raise an IOException or create ActionError objects. I tried the former,
 *  thinking that an error occuring would preempt receiving all the data
 *  (if they are uploading a LARGE file, we don't want to receive all the
 *  data, just to have it go in the bit bucket). Turns out, WAS/HTTP needs 
 *  to drain all the data anyway, so using the latter approach. This allows
 *  semi-meaningful error messages to be returned to the user rather than
 *  a 500 page.   
 *
 * Note, that we need the request object in order to get to the session,
 *  in order to get to the UserDropbox object. 
 *
 * We only support getting the Output stream to write the data
 */
public class WebDboxFileItem implements FileItem {
   protected String  fieldName;
   protected String  contentType;
   protected String  fileName;
   protected boolean isFF;
   protected long    filesize;
   protected long    upsize;
   protected HttpServletRequest request;
   
   public WebDboxFileItem() {}
   public WebDboxFileItem(String fieldName,  String contentType, 
                          boolean isFF,      String fileName,
                          HttpServletRequest request) {
      
      this.fieldName     = fieldName;
      this.contentType   = contentType;
      this.isFF          = isFF;
      this.fileName      = fileName;
      this.request       = request;
      this.upsize        = StreamingUploadOperation.DEFAULT_SIZE;
   }
   
   public void setRequest(HttpServletRequest req) { request = req; }
   
   public InputStream getInputStream()
      throws IOException {
      throw new IOException("Reading from WebDboxFileItem is not supported");
   }
   
   public String getContentType() { return contentType; }
   
   public String getName()        { return fileName; }
   
   public boolean isInMemory()    { return false; }
   
  // My method
   public void setFileSize(long sz) { upsize = sz; }
   
   public long getSize()            { return filesize; }
   
   public byte[] get() { 
      throw new NullPointerException("WebDboxFileItem.get() NOT supported");
   }

   public String getString(String encoding)
      throws UnsupportedEncodingException {
      throw new NullPointerException("WebDboxFileItem.getString not supported");
   }
        
   public String getString() {
      throw new NullPointerException("WebDboxFileItem.getString not supported");
   }

   public void write(File file) throws Exception {
      throw new NullPointerException("WebDboxFileItem.write not supported");
   }

   public void delete() {
     // NOOP
   }

   public String getFieldName()            { return fieldName; }
   
   public void setFieldName(String name)   { fieldName = name; }
    
   public boolean isFormField()            { return isFF; }

   public void setFormField(boolean state) { isFF = state; }

  // Return an OutputStream which wraps the Operation
   public OutputStream getOutputStream() throws IOException {
   
      try {
         HttpSession session = request.getSession(false);
         
         if (session == null) {
            return doErrorReturn("Webdropbox: Fileupload: No Session Found for user");
         }
      
         UserDropbox dropbox = (UserDropbox)session.getAttribute("webdropbox");
         
         if (dropbox == null) {
            return doErrorReturn("Webdropbox: Fileupload: No dropbox found in session");
         }
         if (!dropbox.isConnected()) {
            return doErrorReturn("Webdropbox: Fileupload: Dropbox not connected");
         }
         if (!dropbox.isLoggedIn()) {
            return doErrorReturn("Webdropbox: Fileupload: Dropbox not logged in");
         }
         
         
         String currentpkg=dropbox.getDraftInfoBean().getSelectdDraftPkg();
										 
         Enumeration draftEnum=dropbox.listInOutSandBox(4);
         long pkid=0;
         while(draftEnum.hasMoreElements()) {
            PackageInfo finfo = (PackageInfo)draftEnum.nextElement();
            if (finfo.getPackageName().equals(currentpkg)) {
               pkid = finfo.getPackageId();
               if (finfo.isPackageItar())
               {
               	 if ( !dropbox.isItarSessionCertified())
               	  return doErrorReturn("Webdropbox: Fileupload: Can't access ITAR package data. Please certify your session");
               }
               
               break;
            }
         }
         
         if (pkid == 0) {
            return doErrorReturn("Webdropbox: Fileupload: Package does not exist: " + 
                                 currentpkg);
         }
         
        //Delete file if it exists and is not complete
         
         Enumeration enumc = dropbox.listPackageContents(pkid);
         String fname = getName();
         
         
         File f = new File(fname);
         fname = f.getName();
            
         int colonIndex = fname.indexOf(":");
         if (colonIndex == -1) {
            colonIndex = fname.indexOf("\\\\");
         }
         int backslashIndex = fname.lastIndexOf("\\");

         if (colonIndex > -1 && backslashIndex > -1) {
            fname = fname.substring(backslashIndex + 1);
         }
            
         while(enumc.hasMoreElements()) {
            FileInfo fi = (FileInfo)enumc.nextElement();
            if (fi.getFileName().trim().equals(fname.trim())) {
              
                  	
            	
            	if (fi.getFileStatus() == DropboxGenerator.STATUS_COMPLETE) {
                     
                  return doErrorReturn(
                     "Webdropbox: Fileupload: File already exists in package: " + 
                     fname, "error.filexists.value");
               } 
                  
               dropbox.deleteFileFromPackage(pkid, fi.getFileId());
               break;
            }
         }	
         
         if (fname.trim().length() == 0) {
            return doErrorReturn("Webdropbox: Fileupload: Invalid filename: " + 
                                 fname);
         }
         
         Operation op=dropbox.uploadFile(pkid, fname, upsize);
         
         return new MyOutputStream(dropbox, op);
         
      } catch(IOException ioe) {
        //throw ioe;
         System.out.println("IOException received while prepping upload" + getName());
         ioe.printStackTrace(System.out);
         return doErrorReturn("Webdropbox: Fileupload: Error uploading " + getName() 
                              + ioe.toString(), "error.drafts.severe");
      } catch(Exception e) {
         e.printStackTrace(System.out);
         return doErrorReturn("Webdropbox: Fileupload: Error uploading " + getName() 
                              + e.toString(), "error.drafts.severe");
      }
   }
   
  // Returns an OutputStream which just throws away all data written
  // 
  // Alternatively, it COULD throw an IOException. You choose.
  //
   protected OutputStream doErrorReturn(String errmsg) throws IOException {
      return doErrorReturn(errmsg, null);
   }
   
   protected OutputStream doErrorReturn(String errmsg, String key) throws IOException {
   
     // Create error messages and save on request for Action to display
      ActionErrors errors = new ActionErrors();
      if (key != null) {
        // George seems to use the key for the error msg and field name
        
        // This is a 1.2 API FIX when you fix below TODOTODO
        //errors.add(key, new ActionError(key, true));
         errors.add(key, new ActionError(key, errmsg));
      } else {
        // In 1.2, should be GLOBAL_MESSAGE and FALSE TODOTODO
        // errors.add(ActionError.GLOBAL_ERROR, new ActionError(errmsg, false));
         errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.generic.nolocalplus1", errmsg));
      }
      request.setAttribute("UPLOADER-ERRORS", errors);
   
     //throw new IOException(errmsg);
      
      return new MyOutputStream();
   }
   
  // If the null constructor is used, OS will throw all to bit bucket
   class MyOutputStream extends java.io.OutputStream {
      UserDropbox dropbox;
      Operation op;
      
      public MyOutputStream(UserDropbox d, Operation o) { dropbox = d; op = o; }
      public MyOutputStream() {}
      
      public void close() throws IOException {
         if (op == null) {
         } else {
            boolean successOperation=false;
            try {
               successOperation = dropbox.closeOperation(op);
            } catch(Exception e) {}
            
            if (!successOperation) {
              //throw new IOException("Webdropbox: Fileupload: Success of upload is FALSE: " + getName());
               doErrorReturn("Webdropbox: Fileupload: File upload operation failed during close: " + getName());
              // just bleed the rest off without complaining
               op = null;
            }
         }
      }
      
      public void flush()  throws IOException { }
      
      public void write(int b) throws IOException { 
         byte barr[] = new byte[1];
         barr[0] = (byte)b;
         write(barr, 0, 1);
      }
      public void write(byte b[]) throws IOException { 
         write(b, 0, b.length);
      }
      public void write(byte b[], int ofs, int len) throws IOException { 
         if (op != null) {
            try {
               dropbox.writeFileData(op, b, ofs, len);
               filesize += len;
            } catch(Exception e) {
               try {
                  Operation top = op;
                  op = null; 
                 // This will result in the rest of the written bytes being
                 //  tossed.
                  dropbox.closeOperation(top);
               } catch(Exception eee) {}
               
               doErrorReturn("Webdropbox: Fileupload: File upload operation failed during write (" + filesize + " bytes): [" + getName() + "]");
               
            }
         }
      } 
   }
}
