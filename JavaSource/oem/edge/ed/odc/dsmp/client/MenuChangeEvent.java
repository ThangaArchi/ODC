package oem.edge.ed.odc.dsmp.client;

import javax.swing.JMenu;

import oem.edge.ed.odc.dsmp.common.DSMPEvent;
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

public class MenuChangeEvent extends DSMPEvent {
	public static int ADDMENU = 0;
	public static int REMOVEMENU = 1;
	public JMenu menu;
/**
 * MenuChangeEvent constructor comment.
 * @param reason int
 * @param flags byte
 * @param handle byte
 */
public MenuChangeEvent(int reason, JMenu menu) {
	super(reason, (byte) 0, (byte) 0);
	this.menu = menu;
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2003 10:10:48 AM)
 * @return boolean
 */
public boolean isAddMenu() {
	return reason == ADDMENU;
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2003 10:10:48 AM)
 * @return boolean
 */
public boolean isRemoveMenu() {
	return reason == REMOVEMENU;
}
}
