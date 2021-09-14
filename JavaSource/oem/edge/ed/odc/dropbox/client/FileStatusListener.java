package oem.edge.ed.odc.dropbox.client;

import java.util.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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
 * Insert the type's description here.
 * Creation date: (10/29/2002 1:10:20 PM)
 * @author: Mike Zarnick
 */
public interface FileStatusListener extends EventListener {
/**
 * Insert the method's description here.
 * Creation date: (10/29/2002 1:13:15 PM)
 * @param e FileStatusEvent
 */
void fileStatusAction(FileStatusEvent e);
}
