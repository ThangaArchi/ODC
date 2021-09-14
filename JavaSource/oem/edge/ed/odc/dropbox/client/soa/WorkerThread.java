/*
 * Created on Nov 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.dropbox.client.soa;

/**
 * @author zarnick
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WorkerThread extends Thread {
	public WorkerThread() {
		super();
		setPriority(NORM_PRIORITY);
	}
	public WorkerThread(Runnable r) {
		super(r);
		setPriority(NORM_PRIORITY);
	}
}
