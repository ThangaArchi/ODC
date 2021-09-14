package oem.edge.ed.odc.dropbox.client.soa;

import java.util.*;

import oem.edge.ed.odc.dropbox.client.OptionEvent;
import oem.edge.ed.odc.dropbox.client.OptionListener;
import oem.edge.ed.odc.dsmp.client.FileStatusEvent;
import oem.edge.ed.odc.dsmp.client.FileStatusListener;
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
 * Creation date: (7/23/2002 10:44:09 AM)
 * @author: Mike Zarnick
 */
public class DBEventMulticaster implements DropBoxPnlListener, FileStatusListener, OperationListener, OptionListener {
	protected final EventListener a, b;

	protected DBEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
		this.a = a;
		this.b = b;
	}

	public static DropBoxPnlListener addDropBoxPnlListener(DropBoxPnlListener a, DropBoxPnlListener b) {
		return (DropBoxPnlListener) addInternal(a,b);
	}
	public static FileStatusListener addFileStatusListener(FileStatusListener a, FileStatusListener b) {
		return (FileStatusListener) addInternal(a,b);
	}
	public static OperationListener addOperationListener(OperationListener a, OperationListener b) {
		return (OperationListener) addInternal(a,b);
	}
	public static OptionListener addOptionListener(OptionListener a, OptionListener b) {
		return (OptionListener) addInternal(a,b);
	}
	protected static EventListener addInternal(EventListener a, EventListener b) {
		if (a == null)  return b;
		if (b == null)  return a;
		return new DBEventMulticaster(a, b);
	}

	public static DropBoxPnlListener removeDropBoxPnlListener(DropBoxPnlListener l, DropBoxPnlListener oldl) {
		return (DropBoxPnlListener) removeInternal(l,oldl);
	}
	public static FileStatusListener removeFileStatusListener(FileStatusListener l, FileStatusListener oldl) {
		return (FileStatusListener) removeInternal(l,oldl);
	}
	public static OperationListener removeOperationListener(OperationListener l, OperationListener oldl) {
		return (OperationListener) removeInternal(l,oldl);
	}
	public static OptionListener removeOptionListener(OptionListener l, OptionListener oldl) {
		return (OptionListener) removeInternal(l,oldl);
	}
	protected static EventListener removeInternal(EventListener l, EventListener oldl) {
		if (l == oldl || l == null) {
			return null;
		} else if (l instanceof DBEventMulticaster) {
			return ((DBEventMulticaster)l).remove(oldl);
		} else {
			return l;		// it's not here
		}
	}

	public void dropBoxPnlUpdate(DropBoxPnlEvent e) {
		((DropBoxPnlListener)a).dropBoxPnlUpdate(e);
		((DropBoxPnlListener)b).dropBoxPnlUpdate(e);
	}
	public void fileStatusAction(FileStatusEvent e) {
		((FileStatusListener)a).fileStatusAction(e);
		((FileStatusListener)b).fileStatusAction(e);
	}
	public void operationUpdate(OperationEvent e) {
		((OperationListener)a).operationUpdate(e);
		((OperationListener)b).operationUpdate(e);
	}
	public void optionAction(OptionEvent e) {
		((OptionListener)a).optionAction(e);
		((OptionListener)b).optionAction(e);
	}

	protected EventListener remove(EventListener oldl) {
		if (oldl == a)  return b;
		if (oldl == b)  return a;
		EventListener a2 = removeInternal(a, oldl);
		EventListener b2 = removeInternal(b, oldl);
		if (a2 == a && b2 == b) {
		    return this;	// it's not here
		}
		return addInternal(a2, b2);
	}
}
