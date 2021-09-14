package oem.edge.ed.odc.dropbox.client.soa;

import java.util.*;
/**
 * Interface definition for all registered Operation event listeners.
 */
public interface DropBoxPnlListener extends EventListener {
/**
 * Listener's method called when an operation event is generated.
 */
void dropBoxPnlUpdate(DropBoxPnlEvent e);
}
