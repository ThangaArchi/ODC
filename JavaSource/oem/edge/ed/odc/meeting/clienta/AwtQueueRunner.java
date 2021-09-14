package oem.edge.ed.odc.meeting.clienta;

import java.awt.*;

/**
 * Insert the type's description here.
 * Creation date: (8/3/2002 9:43:19 PM)
 * @author: Mike Zarnick
 */
public class AwtQueueRunner {
	private static EventQueue awtQueue = null;
	private static Runner runner = new Runner();

	private static class Runner extends Component {
		public Runner() {
			super();
			enableEvents(0);
		}
		protected void processEvent(AWTEvent e) {
			RunnableEvent re = (RunnableEvent) e;
			
			if (re.isSynchronous()) {
				synchronized (re) {
					try {
						re.run();
					}
					catch (RuntimeException ex) {
						re.e = ex;
					}
					finally {
						re.notify();
					}
				}
			}
			else {
				re.run();
			}
		}
	}

	private static class RunnableEvent extends AWTEvent {
		static final int EVENT_ID = AWTEvent.RESERVED_ID_MAX + 100;
		private Runnable r;
		private boolean s;
		private boolean d;
		public RuntimeException e;

		public RunnableEvent(Runnable r, boolean s) {
			super(runner,EVENT_ID);
			this.r = r;
			this.s = s;
			this.d = false;
			this.e = null;
		}

		public void run() {
			r.run();
			d = true;
		}

		public boolean isSynchronous() {
			return s;
		}

		private synchronized void waitForCompletion() {
			while (!d) {
				try {
					wait();
				}
				catch (InterruptedException e) {
				}
			}
		}
	}
/**
 * AwtQueueRunner constructor comment.
 */
public AwtQueueRunner() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/2002 9:45:56 PM)
 * @param r java.lang.Runnable
 */
public static void invokeAndWait(Runnable r) {
	if (awtQueue == null)
		awtQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();

	RunnableEvent e = new RunnableEvent(r,true);
	awtQueue.postEvent(e);
	e.waitForCompletion();
	if (e.e != null)
		throw e.e;
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/2002 9:45:56 PM)
 * @param r java.lang.Runnable
 */
public static void invokeLater(Runnable r) {
	if (awtQueue == null)
		awtQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();

	awtQueue.postEvent(new RunnableEvent(r,false));
}
}
