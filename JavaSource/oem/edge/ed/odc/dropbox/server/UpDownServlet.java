package oem.edge.ed.odc.dropbox.server;

import java.io.*;
import oem.edge.ed.odc.dsmp.common.DboxException;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;

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

/*
** Contract:
**
**    Incoming Headers
**
**       COMMAND        =  UPLOAD || DOWNLOAD
**       dboxsessionid  =  the sessionid
**       PACKID         =  packid
**       FILEID         =  fileid
**       SLOTID         =  slotid_for_upload or ofsset for download
**       XFERSIZE       =  number of bytes to xfer
**
**    Outgoing Headers
**
*/

public class UpDownServlet extends HttpServlet {

  // Use a very unlikely static boundry
   static final String boundry    = "3339dRoPbOxBoUnDrY998NeverNEVERnEVER";
   static final String CRLF       = "\r\n";
   
   static Logger log = 
   Logger.getLogger(oem.edge.ed.odc.dropbox.server.UpDownServlet.class);

   class InputStreamDataSource implements DataSource {
      InputStream is;
      
      public InputStreamDataSource(InputStream istrm) {
         is = istrm;
      }
   
      public String getContentType() { 
         return "application/binary";
      }
      public InputStream getInputStream()  throws IOException  { 
         return is;
      }
      public String getName() {
         return "from inputstream";
      }
      public OutputStream getOutputStream() throws IOException {
         throw new IOException("InputStream use only");
      }      
   }

   protected static DropboxAccessSrv dropbox;
   protected static Boolean lock = new Boolean(true);
   
   public void init() {
      synchronized (lock) {
         if (dropbox == null) {
            dropbox = DropboxAccessSrv.getSingleton();
         }
      }
   }
   
   protected void doit(HttpServletRequest req, HttpServletResponse resp) {
      String cmdS       = req.getHeader("command");
      String sesidS     = req.getHeader("dboxsessionid");
      String packidS    = req.getHeader("packid");
      String fileidS    = req.getHeader("fileid");
      String slotidS    = req.getHeader("slotid");
      String sizeS      = req.getHeader("xfersize");
      String encoding   = req.getHeader("encoding");
      
      String accept     = req.getHeader("accept");
      
     // If client can handle multipart, thats good (download issue only)
      boolean multipart = false;
      
      try {
      
         dropbox.setThreadSessionID(sesidS);
         
         if (log.isDebugEnabled()) {
            log.debug("command:"+cmdS+":packid:"+packidS+":fileid:"+fileidS+
                      ":slotid:"+slotidS+":size:"+sizeS+":encode:"+encoding);
         }
         
         long packid = Long.parseLong(packidS);
         long fileid = Long.parseLong(fileidS);
         
        // The slotid is actual id for upload, and ofs for download
         long slotid = Long.parseLong(slotidS);
         long size   = Long.parseLong(sizeS);
         
         if        (cmdS.equalsIgnoreCase("DOWNLOAD")) {
         
            DropboxFileMD5 digest = null;
            
            if (accept != null && accept.toLowerCase().indexOf("multipart/mixed") >= 0) {
               multipart = true;
               digest = new DropboxFileMD5();
               log.debug("Doing Multipart download");
            } else {
               log.debug("!!NO!! Multipart for download");
            }
         
           // If an exception occurs here, the exception info is packed into
           //  the header of the error response
            ComponentInputStream cis = 
               dropbox.downloadPackageItemStream(packid, fileid, slotid, size);
               
            OutputStream out = null;
            try {
               long retszL = cis.getBytesRemaining();
               if (retszL > 0x7fffffff) {
               
                 // TODOTODOTODO ... if multipart, send down exception message
                 //  or perhaps send down exception in header
                  cis.close();
                  throw new Exception("Download size too large");
               }
               
               int retsz = (int)retszL;
               
              // After this point, the response is 'committed', so have to 
              //  manage exception info by jamming it into 2nd section
               try {
               
                  if (multipart) {
                     resp.setContentType("multipart/mixed; boundry="+boundry);
                  } else {
                     resp.setContentType("application/binary");
                     resp.setContentLength(retsz);
                  }
                  
                  out = resp.getOutputStream();
                  byte buf[] = new byte[32768];
                  int totr = retsz;
                  
                 // Put section in place
                  if (multipart) {
                     StringBuffer sb = new StringBuffer();
                     sb.append(CRLF).append("--").append(boundry).append(CRLF);
                     sb.append("content-type: application/binary").append(CRLF);
                     sb.append(CRLF);
                     out.write(sb.toString().getBytes());
                  }
                  
                  log.debug("\n\nDownload : " + totr + "\n\n");
                  
                  while(totr > 0) {
                     int l = buf.length;
                     if (l > totr) l = totr;
                     int r = cis.read(buf, 0, l);
                     if (r > 0) {
                        out.write(buf, 0, r);
                        if (multipart) {
                           digest.update(buf, 0, r);
                        }
                       //System.out.println("\n WRITE : " + r);
                     } else if (r < 0) {
                       // Uh oh, error reading entire expected amount
                        throw new IOException("Ran out of data prior to full data read!");
                     }
                     totr -= r;
                  }
                  
                 // Put section in place
                  if (multipart) {
                     StringBuffer sb = new StringBuffer();
                     sb.append(CRLF).append("--").append(boundry).append(CRLF);
                     sb.append("content-type: application/dropbox").append(CRLF);
                     sb.append("dropbox-datalength: ").append(String.valueOf(retsz));
                     sb.append(CRLF);
                     sb.append("dropbox-datamd5: ").append(digest.asHex());
                     sb.append(CRLF);
                     sb.append(CRLF);
                     sb.append(CRLF).append("--").append(boundry).append("--");
                     sb.append(CRLF);
                     out.write(sb.toString().getBytes());
                  }
               
                  log.debug("\n\nDownload COMPLETE : " + totr + "\n\n");
               } catch(Exception eee) {
                  try {
                     log.error("Error occurred for download");
                     log.error(eee);
                     if (multipart) {
                        StringBuffer sb = new StringBuffer();
                        sb.append(CRLF).append("--").append(boundry).append(CRLF);
                        sb.append("content-type: application/dropbox").append(CRLF);
                        sb.append("dropbox-errormsg: ").append(eee.getMessage());
                        sb.append(CRLF);
                        sb.append(CRLF);
                        sb.append(CRLF).append("--").append(boundry).append("--");
                        sb.append(CRLF);
                        out.write(sb.toString().getBytes());
                     }
                  } catch(Exception nee) {
                     log.error("Error sending err to client!");
                     log.error(nee);
                  }
               }
               
            } finally {
               try {cis.close();} catch(Exception ee) {}
               try {out.close();} catch(Exception ee) {}
            }
         } else if (cmdS.equalsIgnoreCase("DOWNLOADPACKAGE")) {
         
            PackageInputStream cis = 
               dropbox.downloadPackageStream(packid, encoding);
               
            DropboxFileMD5 digest = null;
            
            if (accept != null && accept.toLowerCase().indexOf("multipart/mixed") >= 0) {
               multipart = true;
               digest = new DropboxFileMD5();
               log.debug("Doing Multipart download");
            } else {
               log.debug("!!NO!! Multipart for download");
            }
               
            OutputStream out = null;
            try {
               
               if (multipart) {
                  resp.setContentType("multipart/mixed; boundry="+boundry);
               } else {
                  resp.setContentType("application/binary");
                 //resp.setContentLength(retsz);
               }
               
               out = resp.getOutputStream();
               byte buf[] = new byte[32768];
              //int totr = retsz;
               
              // Put section in place
               if (multipart) {
                  StringBuffer sb = new StringBuffer();
                  sb.append(CRLF).append("--").append(boundry).append(CRLF);
                  sb.append("content-type: application/binary").append(CRLF);
                  sb.append(CRLF);
                  out.write(sb.toString().getBytes());
               }
               
              log.debug("\n\nDownloadPackage \n\n");
               
              int retsize = 0;
              while(true) {
                  int l = buf.length;
                  int r = cis.read(buf, 0, l);
                  if (r > 0) {
                     retsize += r;
                     out.write(buf, 0, r);
                     if (multipart) {
                        digest.update(buf, 0, r);
                     }
                  } else if (r < 0) {
                     break;
                  }
               }
               
              // Put section in place
               if (multipart) {
                  StringBuffer sb = new StringBuffer();
                  sb.append(CRLF).append("--").append(boundry).append(CRLF);
                  sb.append("content-type: application/dropbox").append(CRLF);
                  sb.append("dropbox-datalength: ").append(String.valueOf(retsize));
                  sb.append(CRLF);
                  sb.append("dropbox-datamd5: ").append(digest.asHex());
                  sb.append(CRLF);
                  sb.append(CRLF);
                  sb.append(CRLF).append("--").append(boundry).append("--");
                  sb.append(CRLF);
                  out.write(sb.toString().getBytes());
               }
               
               log.debug("\n\nDownload COMPLETE\n\n");
            } catch(Exception eee) {
               try {
                  log.error("Error occurred for package download");
                  log.error(eee);
                  if (multipart) {
                     StringBuffer sb = new StringBuffer();
                     sb.append(CRLF).append("--").append(boundry).append(CRLF);
                     sb.append("content-type: application/dropbox").append(CRLF);
                     sb.append("dropbox-errormsg: ").append(eee.getMessage());
                     sb.append(CRLF);
                     sb.append(CRLF);
                     sb.append(CRLF).append("--").append(boundry).append("--");
                     sb.append(CRLF);
                     out.write(sb.toString().getBytes());
                  }
               } catch(Exception nee) {
                  log.error("Error occured sending err to client");
                  log.error(nee);
               }
               
            } finally {
               try {cis.close();} catch(Exception ee) {}
               try {out.close();} catch(Exception ee) {}
            }
         } else if (cmdS.equalsIgnoreCase("UPLOAD")) {
         
            DataHandler h = 
               new DataHandler(new InputStreamDataSource(req.getInputStream()));
            
            dropbox.uploadFileSlotToPackageWithHandler(packid, fileid, 
                                                       slotid, false, 
                                                       h);
         } else {
            resp.sendError(resp.SC_BAD_REQUEST, "Bad command: "  + cmdS);
         }
         
      } catch(DboxException dbe) {
         try {
            log.error(dbe);
            if (!resp.isCommitted()) {
               resp.addHeader("dropbox-errormsg", dbe.toString());
               resp.sendError(resp.SC_BAD_REQUEST, dbe.toString());
            }
         } catch (IOException ioe) {}
      } catch(NullPointerException npe) {
         try {
            log.error(npe);
            if (!resp.isCommitted()) {
               resp.addHeader("dropbox-errormsg", npe.toString());
               resp.sendError(resp.SC_BAD_REQUEST, "Missing required header");
            }
         } catch (IOException ioe) {}
      } catch(NumberFormatException nfe) {
         try {
            log.error(nfe);
            nfe.printStackTrace(System.out);
            if (!resp.isCommitted()) {
               resp.addHeader("dropbox-errormsg", nfe.toString());
               resp.sendError(resp.SC_BAD_REQUEST, "Number format error");
            }
         } catch (IOException ioe) {}
      } catch(Exception e) {
         try {
            log.error(e);
            if (!resp.isCommitted()) {
               resp.addHeader("dropbox-errormsg", e.toString());
               resp.sendError(resp.SC_BAD_REQUEST, e.toString());
            }
         } catch (IOException ioe) {}
      } finally {
         dropbox.setThreadSessionID(null);
      }
   }
   
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
      doit(req, resp);
   }
   
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
      doit(req, resp);
   }
}
