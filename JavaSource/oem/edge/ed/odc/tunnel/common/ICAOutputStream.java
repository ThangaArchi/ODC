package oem.edge.ed.odc.tunnel.common;

/**
**  Used to interface JICA to Tunnel. ToDo: Not currently supporting Timeouts,
**   and since its used in the Socket env, we should.
*/

import java.io.*;
public class ICAOutputStream extends OutputStream {
   ICAInputStream str = null;
   protected String name; 
   public void setName(String n) {
      name = n;
      str.setName(n);
   }
   public ICAOutputStream(ICAInputStream s) {
      str = s;
   }
   public void close() throws IOException {
      str.close();
   }
   public void flush() throws IOException {
      if (str.isClosed()) throw new IOException("Stream is Closed");
   }
   public ICAInputStream getInputStream() {
      return str;
   }
   public void write(byte b[]) throws IOException {
      str.write(b, 0, b.length);
   }
   public void write(byte b[], int off, int len) throws IOException {
      str.write(b, off, len);
   }
   public void write(int ch) throws IOException {
      byte b[] = new byte[1];
      b[0] = (byte)ch;
      str.write(b, 0, 1);
   }
}
