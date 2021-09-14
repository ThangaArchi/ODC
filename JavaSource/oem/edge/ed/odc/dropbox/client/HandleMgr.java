package oem.edge.ed.odc.dropbox.client;

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

public class HandleMgr {
	// Marker of which handles are out in circulation.
	private boolean[] handles = new boolean[256];
	// Number of reserved (unused) handles.
	private int reserved = 1;
	// Number of handles in use.
	private int inuse = 0;
	// Next handle available for use.
	private int next = reserved;
/**
 * HandleMgr constructor comment.
 */
public HandleMgr() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (6/16/2003 2:09:33 PM)
 * @return byte
 */
public synchronized Byte getHandle() {
	// If all the handles are used (accounting for reserved handles).
	while (inuse == handles.length - reserved) {
		try {
			//System.out.println("Gotta wait for handle");
			wait();
		}
		catch (Exception e) {
		}
	}

	int h = next;

	// If the next handle is used, go through all unreserved handles
	// looking for an unused one (there should be at least one).
	if (handles[h]) {
		for (h = reserved; h < handles.length && handles[h]; h++);
	}

	// Told there was a handle, but found none, not good.
	if (h == handles.length)
		System.out.println("HandleMgr.getHandle(): woke up and no handles!");

	// Mark handle in use and determine next handle.
	handles[h] = true;
	next = (h == handles.length-1) ? reserved : h + 1;
	inuse++;

	//System.out.println("Issue handle: " + h);
	return new Byte((byte) h);
}
/**
 * Insert the method's description here.
 * Creation date: (6/16/2003 2:32:15 PM)
 * @param h byte
 */
public synchronized void releaseHandle(byte h) {
	int i = h & 0xFF;

	//System.out.println("Release handle: " + i);
	handles[i] = false;
	inuse--;
	next = i;

	notifyAll();
}
/**
 * Insert the method's description here.
 * Creation date: (6/16/2003 2:32:15 PM)
 * @param h byte
 */
public void releaseHandle(Byte h) {
	releaseHandle(h.byteValue());
}
}
