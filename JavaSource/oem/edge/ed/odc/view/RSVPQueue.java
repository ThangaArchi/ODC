package oem.edge.ed.odc.view;

import oem.edge.ed.odc.view.*;
import oem.edge.ed.odc.tunnel.common.*;
import oem.edge.ed.odc.util.*;
import com.ibm.as400.webaccess.common.*;
import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (08/24/00 17:23:45)
 * @author: Administrator
 */
public class RSVPQueue extends Hashtable {
/**
 * RSVPQueue constructor comment.
 */
   public RSVPQueue() {
      super();
   }
/**
 * Insert the method's description here.
 * Creation date: (07/26/00 14:04:19)
 * @return ConfigFile
 */
   public ConfigFile toConfigFile() {
      ConfigFile ini = null;
      ConfigSection section = null;
      Object obj = null;
      InvitationView iv = null;
      DesktopView dv = null;

      if ( this.size() > 0 ) {
         ini = new ConfigFile();
         Enumeration set = this.elements();
         if ( set.hasMoreElements() ) {
            section = new ConfigSection(DesktopCommon.INVITATIONS);
            ini.addSection(section);
         }
         while ( set.hasMoreElements() ) {
            obj = set.nextElement();
            String n = obj.getClass().getName();
           // This table was initially designed as a collection holding
           // invitation views only. Now we want to put other things in
           // there -- YACK!!!!!!!!!!!
            if ( n.indexOf("InvitationView") >= 0 ) {
               iv = (InvitationView) obj;
				// add invitation info as a new section
               section.addSection(iv.toConfigSection());
            } else if ( n.indexOf("DesktopView") >= 0 ) {
               dv = (DesktopView) obj;
				// add desktop info to the root object
               dv.toConfigObject(ini);
            } else if ( n.indexOf("String") >= 0 ) {
               ini.setProperty(DesktopCommon.NEWTOKEN, 
                               (String)obj);
            } else {
               DebugPrint.println(DebugPrint.ERROR,
                                  "RSVPQ: Unknown type!" + n);
            }
         }
      }

      return ini;
   }
}
