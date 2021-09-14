package oem.edge.ed.odc.meeting.clienta;

import java.util.EventListener;
/**
 * Insert the type's description here.
 * Creation date: (7/23/2002 11:27:54 AM)
 * @author: Mike Zarnick
 */
public interface MessageListener extends EventListener {
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 11:28:39 AM)
 * @param e MessageEvent
 */
public void message(MessageEvent e);
}
