package oem.edge.ed.odc.webdropbox.server;

import  oem.edge.ed.odc.util.TimeoutManager;
import  oem.edge.ed.odc.util.TimeoutListener;
import  oem.edge.ed.odc.util.Timeout;
import  oem.edge.ed.odc.tunnel.common.DebugPrint;
import  java.util.Date;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rigts                                 */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */

/**
 * 
 * The WebdropboxTimeout.java handles webdropbox timeout sessions.
 * 
 **/
// Handle the timeout which will clean up our Dropbox session
public class WebdropboxTimeout extends oem.edge.ed.odc.util.Timeout {
      
   public WebdropboxTimeout(String idIn) { 
      super("webdbox"+idIn);
      sessionId = idIn;
   }
      
   public WebdropboxTimeout(Date to, String idIn,
                            TimeoutListener listenerIn) {
      super(to, "webdbox"+idIn, listenerIn);
      sessionId = idIn;
   }
      
   public WebdropboxTimeout(long msDelta, String idIn, 
                            TimeoutListener listenerIn) {
      super(msDelta, "webdbox"+idIn, listenerIn);
      sessionId = idIn;
   }
      
   String sessionId = null;
   public String getSessionId() { return sessionId; }
   
   public void tl_process(Timeout to) {
      try {
         DebugPrint.printlnd(DebugPrint.INFO2,
                             "Dropbox Timedout: " + getSessionId());
             
         UserDropbox dropbox = WebDropboxActions.removeDropboxForId(getSessionId());
            
         if (dropbox != null) {
            dropbox.disconnect();
            
         } else {
            DebugPrint.printlnd(DebugPrint.INFO2, "No dropbox object found");
         }
      } catch(Exception ee) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "Error disconnecting from WebDropbox");
         DebugPrint.printlnd(DebugPrint.ERROR, ee);
      }
   }
}
