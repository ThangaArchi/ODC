package oem.edge.ed.odc.dropbox.client;

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

/**
 * Insert the type's description here.
 * Creation date: (8/5/2004 9:44:10 AM)
 * @author: 
 */
public class OptionEvent extends DSMPEvent {
	/**
	 * Constant used to indicate server package filter settings are changed.
	 * @see #isFilterChange()
	 * @see #reason
	 */
	static public int FILTERCHANGE = 0;
	/**
	 * Constant used to indicate saved window position and size should be discarded.
	 * @see #isResetWindow()
	 * @see #reason
	 */
	static public int RESETWINDOW = 1;
	/**
	 * Constant used to indicate saved table column information should be discarded.
	 * @see #isResetTables()
	 * @see #reason
	 */
	static public int RESETTABLES = 2;
	/**
	 * Constant used to indicate saved split positions should be discarded.
	 * @see #isResetSplit()
	 * @see #reason
	 */
	static public int RESETSPLIT = 3;
/**
 * OptionEvent constructor comment.
 * @param reason int
 * @param flags byte
 * @param handle byte
 */
public OptionEvent(int reason, byte flags, byte handle) {
	super(reason, flags, handle);
}
/**
 * Identifies server package filter settings are changed.
 *
 * @return true if this event represents server package filter settings change.
 */
public boolean isFilterChange() {
	return reason == FILTERCHANGE;
}
/**
 * Identifies a request to discard saved split positions.
 *
 * @return true if this event identifies a request to discard
 * saved split positions.
 */
public boolean isResetSplit() {
	return reason == RESETSPLIT;
}
/**
 * Identifies a request to discard saved table column information.
 *
 * @return true if this event identifies a request to discard saved
 * table column information.
 */
public boolean isResetTables() {
	return reason == RESETTABLES;
}
/**
 * Identifies a request to discard saved window position and size.
 *
 * @return true if this event identifies a request to discard saved
 * window position and size.
 */
public boolean isResetWindow() {
	return reason == RESETWINDOW;
}
}
