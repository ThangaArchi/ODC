package oem.edge.ed.odc.webdropbox.server;

import org.apache.commons.fileupload.*;
import java.io.File;
import javax.servlet.http.HttpServletRequest;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2006-2006                                    */ 
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
 * This file upload class is instantiated by the WebDboxMultipartRequestHandler
 *  which is registered with the Struts module in the struts config controller
 *  section. Its entire purpose is to instantiate our FileItem class as well as
 *  get the Request object to said item.
 */
public class WebDboxFileUpload extends FileUploadBase {

   FileItemFactory fac;
   HttpServletRequest request;
   DefaultFileItemFactory defFac = new DefaultFileItemFactory(1024*10, new File("/tmp"));
   
   public WebDboxFileUpload(HttpServletRequest request) {
      super();
      fac = new MyFileItemFactory();
      this.request = request;
   }
   
   public FileItemFactory getFileItemFactory() {
      return fac;
   }
   public void setFileItemFactory(FileItemFactory factory) {
      fac = factory;
   }  
   
  // Inherited from FileUploadBase ... when item is being created, we want to 
  //  let it know the size ... sigh
  //
  // Zoniks ... IE nor FF send content length up uploaded multipart section!
   protected FileItem createItem(java.util.Map /* String, String */ headers,
                                 boolean isFormField)
      throws FileUploadException {
      FileItem item = super.createItem(headers, isFormField);
      if (!isFormField && item instanceof WebDboxFileItem) {
      
         WebDboxFileItem witem = (WebDboxFileItem)item;
         
         try {
            witem.setFileSize(Long.parseLong(getHeader(headers, "Content-length")));
         } catch(Exception e) {
         }
      }
      return item;
   }
   
   
   class MyFileItemFactory implements FileItemFactory {
      public MyFileItemFactory() {}
      
      public FileItem createItem(String fieldName, 
                                 String contentType, 
                                 boolean isFormField,
                                 String  filename) {
         if (isFormField) {
           // Simple form field ... let the pros handle that
            return defFac.createItem(fieldName, contentType, isFormField, filename);
            
         } else {
            return new WebDboxFileItem(fieldName, contentType, isFormField, 
                                       filename, request);
         }
      }
   }
}




