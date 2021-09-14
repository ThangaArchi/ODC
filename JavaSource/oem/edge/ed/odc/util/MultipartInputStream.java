package oem.edge.ed.odc.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Date;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2006		                         */ 
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
 * MultipartInputStream can be used to access body sections and header field
 *  of a multipart-mixed payload.
 * 
 */
 
public class MultipartInputStream extends java.io.InputStream {
   
   InputStream                       is;
   String                       boundry;
      
   byte                    boundrybytes[];
   byte                        scan_buf[];
   byte                            CRLF[];
   byte                          CRLFdd[];
   byte                          ddCRLF[];
   byte                   CRLFddBoundry[];
   int                      scan_insofs = 0;
   int                     scan_readofs = 0;
   
   boolean                      strmeof = false;
   
   int            scan_boundry_startofs;
   int              scan_boundry_endofs;
   boolean         scan_boundry_partial;
   boolean           scan_boundry_found;
   boolean            scan_boundry_last;
      
   static final byte SEARCHSTARTBOUNDRY = 0;
   static final byte        READHEADERS = 1;
   static final byte           READBODY = 2;
   static final byte         SKIPEPILOG = 3;
   static final byte        NOMOREPARTS = 4;
      
   static final int     MAXHEADERKEYS  = 200;
   static final int     MAXHEADERSIZE  = 32767;
   static final int     MAXSTRINGSIZE  = 4096;
      
   int                            state;
      
   HashMap               sectionHeaders;
   
   boolean dodebug = false;
   boolean doDebug() { return dodebug; }
   void debug(String s) {
      if (dodebug) {
         Date d = new Date();
         System.out.println("MultipartInputStream: " + d.toString() + ": Debug: " + s);
      }
   }
   
   void log(String s) {
      Date d = new Date();
      System.out.println("MultipartInputStream: " + d.toString() + ": Info: " + s);
   }
   void error(String s) {
      Date d = new Date();
      System.out.println("MultipartInputStream: " + d.toString() + ": Error: " + s);
   }
   void error(Throwable t) {
      Date d = new Date();
      System.out.println("MultipartInputStream: " + d.toString() + ": Error: " + 
                         t.getMessage());
      t.printStackTrace(System.out);
   }
      
   public MultipartInputStream(InputStream is, String boundry) throws IOException {
      this.is      = is;
      this.boundry = boundry;
      if (boundry == null) {
         throw new IOException("MultipartInputStream has NULL boundry");
      }
         
      state = SEARCHSTARTBOUNDRY;   // Skip the prolog
            
     // Always half full (if enuf bytes), so will have 2k to work with 
      scan_buf = new byte[4096];
      CRLF = new byte[] {13, 10};
      CRLFdd = new byte[] {13, 10, 45, 45};
      ddCRLF = new byte[] {45, 45, 13, 10};
      boundrybytes = boundry.getBytes();
      CRLFddBoundry = new byte[CRLFdd.length + boundrybytes.length];
      System.arraycopy(CRLFdd, 0, CRLFddBoundry, 0, CRLFdd.length);
      System.arraycopy(boundrybytes, 0, CRLFddBoundry, CRLFdd.length, 
                       boundrybytes.length);
      
   }
      
  // Returns true if there is another section. This is destructive to current section
  //  since this is a stream, have to read to end to see
   public boolean hasNext() throws IOException {
         
      bleedSection();
         
      return state == READHEADERS;
   }
   
  // Returns itself after nudging into next section, otherwise IOException
   public InputStream next() throws IOException {
   
      sectionHeaders = null;
      
      if (!hasNext()) throw new IOException("DataDownloadTransfer: MultipartInputStream has no next section available");
         
      sectionHeaders = readSectionHeaders();
      return this;
   }
      
  // Bleeds the rest of data in current section/body until Boundry is found/consumed
  // Will be in READHEADERS state if another section, NOMOREPARTS if done
   protected void bleedSection() throws IOException {
      
      if (state == READHEADERS || 
          state == NOMOREPARTS) {
         return;
      }
         
      byte buf[] = new byte[1024];
         
     // Read all bytes from current section body. State will be stamped
      if (state == READBODY) {
         while (read(buf, 0, buf.length) >= 0);
         return;
      }
         
     // Read rest of bytes from main stream till done
      if (state == SKIPEPILOG) {
         while (is.read(buf, 0, buf.length) >= 0);
         scan_readofs = scan_insofs = 0;
         state = NOMOREPARTS;
         return;
      }
         
     // Bleed until none left in section will move state along as well
      while(iread(buf, 0, buf.length) >= 0);
         
   }
      
  // Validate that header string is valid, and add to map
  //  Assuming totsize is "size" of map on entry, we return the
  //  adjusted size for the new header being added/merged.
   public String getHeader(String key) {
      String ret = null;
      if (sectionHeaders != null) {
         ret = (String)sectionHeaders.get(key.toLowerCase());
      }
      return ret;
   }
   
   public HashMap getHeaders() {
      return (HashMap)sectionHeaders.clone();
   }
      
  // Validate that header string is valid, and add to map
  //  Assuming totsize is "size" of map on entry, we return the
  //  adjusted size for the new header being added/merged.
   protected int addHeader(HashMap map, String head, int totsize) throws IOException {
      
     // If we have whitespace at beginning, Illegal
      if (Character.isWhitespace(head.charAt(0))) {
         throw new IOException("Multipart header starts with whitespace");
      }
         
      int idx = head.indexOf(":");
         
      if (idx <= 0) {
         throw new IOException("Multipart header value is invalid format [" + 
                               head + "]");
      }
         
      String key = head.substring(0, idx).trim().toLowerCase();
      String val = head .substring(idx+1).trim();
         
      String curval = (String)map.get(key);
      if (curval != null) {
         map.put(key, curval + " , " + val);
         totsize += val.length() + 3;
            
      } else {
         map.put(key, val);
         totsize += val.length();
      }
      
      if (totsize > MAXHEADERSIZE) {
         throw new IOException("MultipartInputStream: Max headersize exceeded = " + 
                               totsize);
      }
      if (map.size() > MAXHEADERKEYS) {
         throw new IOException("MultipartInputStream: Max headers exceeded = " + 
                               map.size());
      }
      
      return totsize;
   }
      
  // Assumes that we are positioned to read headers of section
   protected HashMap readSectionHeaders() throws IOException {
      
      if (state != READHEADERS) {
         throw new IOException("Asked to read headers of multipart message, but not in READHEADERS state");
      }
         
      HashMap ret = new HashMap();
         
     // Read CRLF terminated strings and compose headers from them
     // If we exceed MAXHEADERSIZE value for total header size, IOException
      int headsize = 0;
      String lasths = null;
      String nexths = null;
      while(true) {
         String hs = readln();
            
        // Bare CRLF means end of header section. Start reading body
         if (hs.length() == 0) {
            state = READBODY;
            break;
         }
            
        // If we have whitespace at beginning, we have to join the lines
         if (Character.isWhitespace(hs.charAt(0))) {
            
           // If no previous line, error
            if (lasths == null) {
               throw new IOException("Multipart header starts with whitespace");
            }
               
           // Just join them as is ...
            lasths = lasths + hs;
               
           // But error if its getting too big
            if (lasths.length() > MAXSTRINGSIZE) {
               throw new IOException("Header exceeded max size of " + MAXSTRINGSIZE);
            }
         } else {
            if (lasths != null) addHeader(ret, lasths, headsize);
            lasths = hs;
         }
      }   
         
      if (lasths != null) {
         headsize = addHeader(ret, lasths, headsize);
      }
         
      return ret;
   }
      
  //
  // Return a string terminated by CRLF (with the CRLF stripped off)
  // If returned string will be greater than MAXSTRINGSIZE, then IOException
  // 
  // Returns null if EOF ... IOException if hit EOF and string NOT terminated
  //  by CRLF
  //
   protected String readln() throws IOException {
      
      
      StringBuffer sb = new StringBuffer();
      
      int found = 0;
      fillScanBuffer();
      
      while(scan_readofs < scan_insofs) {
      
        // Get data into scan buffer.
         
        // Search for CRLF
         for(int i=scan_readofs; i < scan_insofs; i++) {
            if (found == 0 && scan_buf[i] == 13) {
               found=1;
            } else if (found == 1) {
               if (scan_buf[i] == 10) {
                 // If we have a CRLF and some data, add it in
                  if (i > scan_readofs + 1) {
                     sb.append(new String(scan_buf, scan_readofs, i-scan_readofs-1));
                  }
                  scan_readofs = i+1;
                  
                  if (sb.length() > MAXSTRINGSIZE) {
                     throw new IOException("Header size > max allowed: " + sb.length());
                  }
                  
                  return sb.toString();
                  
               } else {
                  found = 0;
               }
            }
         }
         
        // We are still searching ...
        // If we have any data which is NOT CR, convert it before read
         int scanlen = scan_insofs-scan_readofs-found;
         
         if (scanlen > 0) {
            sb.append(new String(scan_buf, scan_readofs, scanlen));
            scan_readofs += scanlen;
         }
         
         if (sb.length() > MAXSTRINGSIZE) {
            throw new IOException("Header size > max allowed: " + sb.length());
         }
         
         fillScanBuffer();
      }
      
      if (sb.length() > 0) {
         throw new IOException("MultipartInputStream: Line not terminated with CRLF");
      }
      
      return "";
   }
      
  // This routine scans the data in the buffer for the boundry, and sets up variables
  //  accordingly. 
  //
  //  scan_boundry_startofs   - ofs into scan_buf of start byte of match
  //  scan_boundry_endofs     - ofs into scan_buf of last  byte of match
  //  scan_boundry_partial    - true if partial boundry matched 
  //  scan_boundry_found      - true if entire boundry matched    (partial is also true)
  //  scan_boundry_last       - true if boundry was LAST boundry  (found   is also true)
  //
  //  CRLF--boundrybytes<-->CRLF
  //
   protected void scanForBoundry() {
      scan_boundry_partial = scan_boundry_found = scan_boundry_last = false;
      scan_boundry_startofs = scan_boundry_endofs = -1;
      
      int i, cbi;
      for(cbi=i=scan_readofs; i < scan_insofs; i++) {
      
        // Find initial CR
         for(; i < scan_insofs; i++) {
            if (scan_buf[i] == 13) {
               scan_boundry_partial = true;
               break;
            }
         }
         
        // If we don't have a good starting point, done
         if (!scan_boundry_partial) break;
         
         int left = scan_insofs-i;
         
        // Get len to check
         int chklen = CRLFddBoundry.length;
         
        // Adjust len to check based on smallest val
         int tochk = chklen;
         if (chklen > left) tochk = left;
         
        // If we don't match we are no good ... start over
         cbi = i-1;
         for(int j=0; j < tochk ; j++) {
            cbi++;
            if (CRLFddBoundry[j] != scan_buf[cbi]) {
               scan_boundry_partial = false;
               break;
            }
         }
         
        // If we don't have a good match, we shift to next i byte
         if (!scan_boundry_partial) continue;
         
        // If we are out of bytes 
         if (left <= chklen) {
            break;
         }
         
        // Wow, we have CRLF--boundryvalue   ... so we are now looking for
        //  CRLF or --CRLF to complete the picture
         
        // Check agains --CRLF if first one is dash, otherwise, use CRLF
         byte arr[] = ddCRLF;
         
         if (scan_buf[cbi+1] != arr[0]) {
            arr = CRLF;
         }
         
         left = scan_insofs-cbi;
         
        // Get len to check
         chklen = arr.length;
         
        // Adjust len to check based on smallest val
         tochk = chklen;
         if (chklen > left) tochk = left;
         
        // If we don't match we are no good ... start over
         for(int j=0; j < tochk ; j++) {
            cbi++;
            if (arr[j] != scan_buf[cbi]) {
               scan_boundry_partial = false;
               break;
            }
         }
         
        // If we don't have a good match, we shift to next i byte
         if (!scan_boundry_partial) continue;
         
        // If we ran out of bytes ... done
         if (left < chklen) {
            break;
         }
        
        // Yippe, we found an offical boundry
         scan_boundry_found = true;
         if (arr == ddCRLF) {
            scan_boundry_last = true;
         }
         
         break;
         
      }
      
      if (scan_boundry_partial) {
         scan_boundry_startofs = i;
         scan_boundry_endofs   = cbi;
      }
   }
      
  //
  // Reads data from scanbuffer, and returns data which is NOT
  //  boundry identifier.
  //
   protected int iread(byte buf[], int ofs, int len) throws IOException {
   
      if (state == NOMOREPARTS) return -1;
      
     // Get scan buffer at least half full (if we have the data)
      fillScanBuffer();
         
     // search for possible boundry 
      scanForBoundry();
         
      int scanlen = scan_insofs - scan_readofs;
         
     // If we have some part of the boundry in scope
      if (scan_boundry_startofs >= 0) {
            
        // If we have some data prior to the boundry, return it
         if (scan_readofs < scan_boundry_startofs) {
            scanlen = scan_boundry_startofs - scan_readofs;
            if (scanlen < len) len = scanlen;
            System.arraycopy(scan_buf, scan_readofs, buf, ofs, len);
            scan_readofs += len;
            return len;
         }
            
        // Ok, we SHOULD have a complete boundry now, cause its the first
        //  byte in scanbuf, and buffer is big enuf for any supported 
        //  boundry string (70chars or less, including leading/trailing -- and
        //  leading/trailing CRLF).  
        //
        // If we do NOT have a complete boundry, then IOException
        //
        // Set the next state and adjust scan ofs pointers
        //
        
         if (!scan_boundry_found) {
            throw new IOException("MultipartInputStream: Have a partial boundry, and buffer is left jusitifed");
         }
         
         if (state == SEARCHSTARTBOUNDRY) {
            if (scan_boundry_last) {
               throw new IOException("MultipartInputStream: Found LAST boundry while searching for start");
            }
            state = READHEADERS;
            scan_readofs = scan_boundry_endofs+1;
            return -1;
         }
         
         if (state == READBODY) {   
            if (scan_boundry_last) state = SKIPEPILOG;
            else                   state = READHEADERS;
            
            scan_readofs = scan_boundry_endofs+1;
         
            return -1;
         }
         
         return -1;
            
      } else if (scanlen == 0) {
         
        // No boundry parts found, but no data ... we are done!
         state = NOMOREPARTS;
         return -1;
           
      } else {
            
        // No boundry parts found, return all the data we can
           
         if (scanlen < len) len = scanlen;
         System.arraycopy(scan_buf, scan_readofs, buf, ofs, len);
         scan_readofs += len;
         return len;
      }
   }      
      
  //
  // Reads data from regular inputstream, into scan_buf. Buffer is always
  //  at least half full if we have not run out of data. 
  //
   protected void fillScanBuffer() throws IOException {
      
     // Number of bytes in buffer
      int scanlen = scan_insofs - scan_readofs;
         
     // If its empty, put it back to 0
      if (scanlen <= 0) {
         scan_readofs = scan_insofs = 0;
      } else {
      
        // Shift bytes to index 0 if read ofs is > half the buffer
         if (!strmeof && scan_readofs >= (scan_buf.length >> 1)) {
            System.arraycopy(scan_buf, scan_readofs, scan_buf, 0, 
                             scanlen);
            scan_readofs = 0;
            scan_insofs  = scanlen;                             
         }
      }
                  
     // Try to fill the scan buffer
      while(!strmeof && scan_insofs < scan_buf.length-1) {
         int r = is.read(scan_buf, scan_insofs, scan_buf.length - scan_insofs);
         if (r > 0) {
            scan_insofs += r;
         } else if (r < 0) {
            strmeof = true;
            break;
         }
      }         
   }
      
      
  //--------------------------
  // InputStream Methods
  //--------------------------
   public int read() throws IOException {
      byte buf[] = new byte[1];
      int r;
      while((r=read(buf, 0, 1)) == 0);
      if (r == 1) r = 0xff & (int)buf[0];
      return r;
   }
   public int read(byte buf[]) throws IOException {
      return read(buf, 0, buf.length);
   }
      
  //
  // Get data from current section
  //
  // Continues to return data until boundry is found. That signifies
  //  EOF for the section, we set foundboundry to true, and return -1
  //  for all other calls until foundboundry is false
  //
   public int read(byte buf[], int ofs, int len) throws IOException {
      
      if (state != READBODY) return -1;
         
      return iread(buf, ofs, len);
   }         
      
   public void close() throws IOException {
      is.close();
      scan_readofs=scan_insofs=0;
      state = NOMOREPARTS;
   }
      
   public long skip(long len) throws IOException {
      byte buf[] = new byte[1024];
      return read(buf, 0, buf.length);
   }
      
  // The Who Cares methods
   public int available() throws IOException {
      return 0;
   }
   public void mark(int readLimit) {
   }
   public void reset() throws IOException {
      throw new IOException("Marking not supported");
   }
   public boolean markSupported() {
      return false;
   }
      
  // Manual testcase: pass in file and boundry, will parse and spit out headers and section results. 
  //  Use all text      
   static public void main(String args[]) {
      try { 
         FileInputStream fis = new FileInputStream(args[0]);
      
      
         MultipartInputStream mis = new MultipartInputStream(fis, args[1]);
         byte buf[] = new byte[1024];
         while(mis.hasNext()) {
            mis.next();
            System.out.println("\n---Headers---\n" + mis.getHeaders().toString() + "\n---Data----");
            while(true) {
               int r = mis.read(buf);
               if (r > 0) {
                  System.out.print(new String(buf, 0, r));
               } else if (r < 0) {
                  break;
               }
            }
         }
      } catch(IOException ioe) {
         ioe.printStackTrace(System.out);
      }
   }
}
