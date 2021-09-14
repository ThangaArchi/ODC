package oem.edge.ed.odc.ftp.client;

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
 * Creation date: (10/29/2002 1:09:19 PM)
 * @author: Mike Zarnick
 */
public class FTPStatusEvent extends DSMPEvent {
	static public int INTERIM = 0;
	static public int END = 1;
	public int id;
	public Object status = null;
/**
 * FTPStatusEvent constructor comment.
 * @param t int
 * @param f byte
 * @param h byte
 */
public FTPStatusEvent(int t, byte f, byte h) {
	super(t, f, h);
}
/**
 * FTPStatusEvent constructor comment.
 * @param t int
 * @param f byte
 * @param h byte
 */
public FTPStatusEvent(int t, byte f, byte h, int id, Object status) {
	this(t, f, h);
	this.id = id;
	this.status = status;
}
/**
 * Insert the method's description here.
 * Creation date: (10/29/2002 1:48:57 PM)
 * @return boolean
 */
public boolean isEnd() {
	return reason == END;
}
/**
 * Insert the method's description here.
 * Creation date: (10/29/2002 1:48:57 PM)
 * @return boolean
 */
public boolean isInterim() {
	return reason == INTERIM;
}
}
