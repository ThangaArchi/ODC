package oem.edge.ed.odc.dropbox.client;

import java.util.EventListener;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2006                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rights                                */
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
 * Creation date: (8/5/2004 9:46:44 AM)
 * @author: 
 */
public interface OptionListener extends EventListener {
/**
 * Insert the method's description here.
 * Creation date: (8/5/2004 9:48:43 AM)
 * @param e oem.edge.ed.odc.dropbox.client.OptionEvent
 */
public void optionAction(OptionEvent e);
}
