package oem.edge.ed.odc.webdropbox.server;

import org.apache.struts.upload.*;
import org.apache.commons.fileupload.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.Globals;

import javax.servlet.http.*;
import javax.servlet.*;

/**
 * We provide our own MultipartRequestHandler, which is used to handle file upload
 *  type requests. It would be nice to use the standard handler 
 *  (CommonsMultipartRequestHandler), but he hardcodes DiskFileUpload usage, and
 *  there are no hooks to instantiate our own fileuploader. The default uploader
 *  [DiskFileUploader] caches the file locally before we can get our hands on it
 *  That is a no-no with ITAR data, and generally is a pain to manage (eg. many
 *  large files stored in temp space ... we could easily run out).  Our
 *  uploader instance will put the file to dropbox directly using the UserDropbox
 *  instance.  
 *
 * So, when the Action for the upload is actually called ... all the work will 
 *  already be done.
 *
 * This class needs to be registered via struts config property for the AddFiles
 *
 * Would have been nice to just replace the DiskFileUpload object with my own
 * ... oh well.
 *
 */
/*
 * The handleRequest routine was derived from the Commons struts code carrying the
 *  following license. Also the CommonsFormFile was taken lockstock, as I could not
 *  simply ref it outside of the package. Sigh:
 *
 *    $Id: CommonsMultipartRequestHandler.java 54929 2004-10-16 16:38:42Z germuska $ 
 *   
 *    Copyright 1999-2004 The Apache Software Foundation.
 *    
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *    
 *         http://www.apache.org/licenses/LICENSE-2.0
 *    
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * 
 */
 
// Extend CommonsMPRH rather than implement MPRH to get easy access to 
//  CommonsFormFile inner class. This stinks. If CMPRH had just supported
//  customized FileUpload would have been all set OR, of the elementsXXX
//  were not private, would have been alot less messy!
public class WebDboxMultipartRequestHandler extends CommonsMultipartRequestHandler {
    //implements MultipartRequestHandler {

//   ActionServlet servlet;
//   ActionMapping mapping;
   Hashtable elementsALL, elementsTEXT, elementsFILE;
   
   public WebDboxMultipartRequestHandler() { super(); }
   
//   public void setServlet(ActionServlet servlet) { this.servlet = servlet; }

//   public void setMapping(ActionMapping mapping) { this.mapping = mapping; }

//   public ActionServlet getServlet() { return servlet; }

//   public ActionMapping getMapping() { return mapping; }

   public void handleRequest(HttpServletRequest request)
      throws ServletException {
      
      WebDboxFileUpload upload = new WebDboxFileUpload(request);
      upload.setHeaderEncoding(request.getCharacterEncoding());
      
     // Create the hash tables to be populated.
      elementsTEXT = new Hashtable();
      elementsFILE = new Hashtable();
      elementsALL = new Hashtable();

     // Parse the request into file items.
      List items = null;
      try {
         items = upload.parseRequest(request);
      } catch (FileUploadException e) {
         throw new ServletException(e);
      }

     // Partition the items into form fields and files.
      Iterator iter = items.iterator();
      while (iter.hasNext()) {
         FileItem item = (FileItem) iter.next();

         if (item.isFormField()) {
         
            String name  = item.getFieldName();
            String value = item.getString();
         
            if (request instanceof MultipartRequestWrapper) {
               MultipartRequestWrapper wrapper = (MultipartRequestWrapper) request;
               wrapper.setParameter(name, value);
            }         
            
            String[] oldArray = (String[]) elementsTEXT.get(name);
            String[] newArray;
            
            if (oldArray != null) {
               newArray = new String[oldArray.length + 1];
               System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
               newArray[oldArray.length] = value;
            } else {
               newArray = new String[] { value };
            }
            
            elementsTEXT.put(name, newArray);
            elementsALL.put(name, newArray);            
            
         } else {
            FormFile formFile = new CommonsFormFile(item);
            elementsFILE.put(item.getFieldName(), formFile);
            elementsALL.put(item.getFieldName(), formFile); 
         }
      }
      
   }

   public Hashtable getTextElements() {
      return elementsTEXT;
   }
    
   public Hashtable getFileElements() {
      return elementsFILE;
   }

   public Hashtable getAllElements() {
      return elementsALL;
   }

  // Consider deleting files from server
   public void rollback() {
   }

   public void finish() {
   }
   
    /**
     * This class implements the Struts <code>FormFile</code> interface by
     * wrapping the Commons FileUpload <code>FileItem</code> interface. This
     * implementation is <i>read-only</i>; any attempt to modify an instance
     * of this class will result in an <code>UnsupportedOperationException</code>.
     */
    static class CommonsFormFile implements FormFile, Serializable {

        /**
         * The <code>FileItem</code> instance wrapped by this object.
         */
        FileItem fileItem;


        /**
         * Constructs an instance of this class which wraps the supplied
         * file item.
         *
         * @param fileItem The Commons file item to be wrapped.
         */
        public CommonsFormFile(FileItem fileItem) {
            this.fileItem = fileItem;
        }


        /**
         * Returns the content type for this file.
         *
         * @return A String representing content type.
         */
        public String getContentType() {
            return fileItem.getContentType();
        }


        /**
         * Sets the content type for this file.
         * <p>
         * NOTE: This method is not supported in this implementation.
         *
         * @param contentType A string representing the content type.
         */
        public void setContentType(String contentType) {
            throw new UnsupportedOperationException(
                    "The setContentType() method is not supported.");
        }


        /**
         * Returns the size, in bytes, of this file.
         *
         * @return The size of the file, in bytes.
         */
        public int getFileSize() {
            return (int)fileItem.getSize();
        }


        /**
         * Sets the size, in bytes, for this file.
         * <p>
         * NOTE: This method is not supported in this implementation.
         *
         * @param filesize The size of the file, in bytes.
         */
        public void setFileSize(int filesize) {
            throw new UnsupportedOperationException(
                    "The setFileSize() method is not supported.");
        }


        /**
         * Returns the (client-side) file name for this file.
         *
         * @return The client-size file name.
         */
        public String getFileName() {
            return getBaseFileName(fileItem.getName());
        }


        /**
         * Sets the (client-side) file name for this file.
         * <p>
         * NOTE: This method is not supported in this implementation.
         *
         * @param fileName The client-side name for the file.
         */
        public void setFileName(String fileName) {
            throw new UnsupportedOperationException(
                    "The setFileName() method is not supported.");
        }


        /**
         * Returns the data for this file as a byte array. Note that this may
         * result in excessive memory usage for large uploads. The use of the
         * {@link #getInputStream() getInputStream} method is encouraged
         * as an alternative.
         *
         * @return An array of bytes representing the data contained in this
         *         form file.
         *
         * @exception FileNotFoundException If some sort of file representation
         *                                  cannot be found for the FormFile
         * @exception IOException If there is some sort of IOException
         */
        public byte[] getFileData() throws FileNotFoundException, IOException {
            return fileItem.get();
        }


        /**
         * Get an InputStream that represents this file.  This is the preferred
         * method of getting file data.
         * @exception FileNotFoundException If some sort of file representation
         *                                  cannot be found for the FormFile
         * @exception IOException If there is some sort of IOException
         */
        public InputStream getInputStream() throws FileNotFoundException, IOException {
            return fileItem.getInputStream();
        }


        /**
         * Destroy all content for this form file.
         * Implementations should remove any temporary
         * files or any temporary file data stored somewhere
         */
        public void destroy() {
            fileItem.delete();
        }


        /**
         * Returns the base file name from the supplied file path. On the surface,
         * this would appear to be a trivial task. Apparently, however, some Linux
         * JDKs do not implement <code>File.getName()</code> correctly for Windows
         * paths, so we attempt to take care of that here.
         *
         * @param filePath The full path to the file.
         *
         * @return The base file name, from the end of the path.
         */
        protected String getBaseFileName(String filePath) {

            // First, ask the JDK for the base file name.
            String fileName = new File(filePath).getName();

            // Now check for a Windows file name parsed incorrectly.
            int colonIndex = fileName.indexOf(":");
            if (colonIndex == -1) {
                // Check for a Windows SMB file path.
                colonIndex = fileName.indexOf("\\\\");
            }
            int backslashIndex = fileName.lastIndexOf("\\");

            if (colonIndex > -1 && backslashIndex > -1) {
                // Consider this filename to be a full Windows path, and parse it
                // accordingly to retrieve just the base file name.
                fileName = fileName.substring(backslashIndex + 1);
            }

            return fileName;
        }

        /**
         * Returns the (client-side) file name for this file.
         *
         * @return The client-size file name.
         */
        public String toString() {
            return getFileName();
        }
    }   
}
