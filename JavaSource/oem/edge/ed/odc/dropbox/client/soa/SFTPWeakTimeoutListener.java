package oem.edge.ed.odc.dropbox.client.soa;

import java.lang.ref.WeakReference;
import oem.edge.ed.odc.util.TimeoutListener;
import oem.edge.ed.odc.util.Timeout;
import oem.edge.ed.odc.dropbox.client.soa.sftpDropbox;

public class SFTPWeakTimeoutListener implements TimeoutListener {
      
   WeakReference wr;
      
   public SFTPWeakTimeoutListener(sftpDropbox ref) {
      wr = new WeakReference(ref);
   }
      
  // We process the Timeout here for REFRESHSESSIONID
   public void tl_process(Timeout t) {
      sftpDropbox sftpdbox = (sftpDropbox)wr.get();
      if (sftpdbox != null) {
         try {
            sftpdbox.refreshSession();
         } catch(Exception e) {}
      }
   }
}
  
