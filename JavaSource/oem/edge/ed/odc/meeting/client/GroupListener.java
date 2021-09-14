package oem.edge.ed.odc.meeting.client;

import java.util.EventListener;
/**
 * Insert the type's description here.
 * Creation date: (7/23/2002 11:16:21 AM)
 * @author: Mike Zarnick
 */
public interface GroupListener extends EventListener {
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 11:17:12 AM)
 * @param e GroupEvent
 */
public void groupEvent(GroupEvent e);
}
