package oem.edge.ed.odc.meeting.client;

import java.util.EventListener;

/**
 * Insert the type's description here.
 * Creation date: (7/23/2002 9:53:05 AM)
 * @author: Mike Zarnick
 */
public interface PresenceListener extends EventListener {
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 9:53:59 AM)
 * @param e PresenceEvent
 */
public void presenceChanged(PresenceEvent e);
}
