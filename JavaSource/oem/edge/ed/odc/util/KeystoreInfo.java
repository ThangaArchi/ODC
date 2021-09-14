package oem.edge.ed.odc.util;

import java.io.*;
public class KeystoreInfo {
   InputStream ksstrm = null;
   String ksfile = null;
   String kspw   = null;
   public KeystoreInfo(String file, String pw) {
      ksfile = file;
      kspw = pw;
   }
   public KeystoreInfo(InputStream strm, String pw) {
      ksstrm = strm;
      kspw   = pw;
   }
         
   public String getKeystoreFile()            { return ksfile; }
   public String getKeystorePassword()        { return kspw;   }
   public InputStream  getKeystoreStream() throws IOException {
      if (ksfile != null) {
         try {
            if (ksstrm != null) {
               ksstrm.close();
            }
         } catch(Exception e) {}
         ksstrm = new FileInputStream(ksfile);
      } else if (ksstrm == null) {
         throw new IOException("No file or stream set for KeystoreInfo");
      }
      return ksstrm;
   }
         
   public void setKeystoreFile(String file)   { ksfile = file; }
   public void setKeystorePassword(String pw) { kspw   = pw;   }
   
   
}
