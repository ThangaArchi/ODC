package oem.edge.ed.odc.dropbox.client;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import oem.edge.ed.odc.dsmp.client.AboutWindow;
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

public class DropBoxApplet extends JApplet implements DropBoxPnlListener, Runnable {
	private DropBoxPnl dropBoxPnl;
	private JDialog initDlg = null;
	private long initDlgShown;
	private Frame owner = null;

	private String[] args = null;

	private Method eval = null;
	private Object jsWindow = null;

	private boolean started = false;

	private class AppletWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.out.println("Window is closing!");
		}
	}

	public DropBoxApplet() {
		super();
	}

	public void closeWindow() {
		if (eval == null) {
			System.out.println("CloseWindow: no access to JavaScript!");
			return;
		}

		try {
			Object[] evalParms = { "self.setTimeout(\"self.close()\",500);" };
			eval.invoke(jsWindow,evalParms);
		}
		catch (IllegalAccessException iae) {
			System.out.println("Illegal access exception: " + iae.getMessage() + "\n");
		}
		catch (IllegalArgumentException iarge) {
			System.out.println("Illegal argument exception: " + iarge.getMessage() + "\n");
		}
		catch (InvocationTargetException ite) {
			System.out.println("Invocation target exception: " + ite.getMessage() + "\n");
		}
	}
	public void dropBoxPnlUpdate(DropBoxPnlEvent e) {
		if (e.isTitle()) {
			// Change the title.
			setTitle(e.title);
		}
		else if (e.isHide()) {
			// Want to leave the splash screen up for at least 3 seconds.
			long timeLeft = 3000 - (System.currentTimeMillis() - initDlgShown);
			if (timeLeft > 0) {
				try {
					Thread.sleep(timeLeft);
				} catch (Exception e1) {
				}
			}

			SwingUtilities.invokeLater(new MethodRunner(initDlg,"hideAbout"));
		}
		else if (e.isShow()) {
			// We're an applet, we're already showing. Can try to use
			// JavaScript to move and size the window though (if we can
			// figure out how to use it to get the size and position).
		}
		else if (e.isSavePreferences()) {
			// Can do this if we can figure out how to get JavaScript 
			// values back into Java.
		}
		else if (e.isExit()) {
			// Use JavaScript to close the window.
			closeWindow();
		}
	}
	public void init() {
		// Try to set-up eval for JavaScript invocations.
		try {
			// We need the JSObject class and its getWindow(Applet) and eval(String) methods
			Class js = Class.forName("netscape.javascript.JSObject");
			Class[] gwArgs = { Applet.class };
			Class[] evalArgs = { String.class };
			Method getWindow = js.getMethod("getWindow",gwArgs);
			eval = js.getMethod("eval",evalArgs);

			Object[] gwParms = { DropBoxApplet.this };
			jsWindow = getWindow.invoke(null,gwParms);
			
			if (jsWindow == null)
				eval = null;
		}
		catch (ClassNotFoundException cnfe) {
			System.out.println("Class not found: " + cnfe.getMessage() + "\n");
		}
		catch (NoSuchMethodException nsme) {
			System.out.println("No such method: " + nsme.getMessage() + "\n");
		}
		catch (IllegalAccessException iae) {
			System.out.println("Illegal access exception: " + iae.getMessage() + "\n");
		}
		catch (IllegalArgumentException iarge) {
			System.out.println("Illegal argument exception: " + iarge.getMessage() + "\n");
		}
		catch (InvocationTargetException ite) {
			System.out.println("Invocation target exception: " + ite.getMessage() + "\n");
		}

		// Locate the owning frame of the applet.
		Component p = this;
		while (p != null && !(p instanceof Frame)) p = p.getParent();
		if (p == null) p = new Frame();
		owner = (Frame) p;

		// Validate a minimal Java 2 level.
		boolean isJavaOk = true;

		// If Java 2 level is ok, initialize GUI.
		if (isJavaOk) {
			// Get our parameters.
			String token = getParameter("-CH_TOKEN");
			String url = getParameter("-URL");
			String context = getParameter("-CH_SERVLETCONTEXT");

			// No URL provided? Derive it.
			if (url == null) {
				URL docBase = this.getDocumentBase();
				url = docBase.getProtocol() + "://" + docBase.getHost();
				if (docBase.getPort() != -1)
					url += ":" + docBase.getPort();
				if (context != null)
					url += context;
			}

			// If no URL, can't do a thing, so create an error panel instead.
			if (url == null) {
				JPanel pnl = new JPanel();
				JLabel lbl = new JLabel("Dropbox Error: required parameters not provided by server!");
				pnl.setLayout(new BorderLayout());
				lbl.setHorizontalAlignment(JLabel.CENTER);
				lbl.setHorizontalTextPosition(JLabel.CENTER);
				pnl.add(lbl,"Center");
				setContentPane(pnl);
				started = true; // Nothing to do after this...
			}
			
			// Have everything we need, ready to go.
			else {
				setName("DropBox");
				setTitle("Drop Box");
				setSize(632, 553);
		
				dropBoxPnl = new DropBoxPnl(owner,getRootPane());
				dropBoxPnl.addDropBoxPnlListener(this);
				setJMenuBar(dropBoxPnl.getDropBoxJMenuBar());
				setContentPane(dropBoxPnl);
				validate();

				// Build the args for later.
				int i = 3; // For url and no updates
				if (token != null) i += 2;
				args = new String[i];
				args[0] = "-URL";
				args[1] = url;
				args[2] = "-CH_NOUPDATE";
				if (token != null) {
					args[3] = "-CH_TOKEN";
					args[4] = token;
				}
				
				// Add a window listener to the owner frame.
				owner.addWindowListener(new AppletWindowListener());
			}
		}

		// Otherwise, initialize an error panel.
		else {
			JPanel pnl = new JPanel();
			JLabel lbl = new JLabel("Error: Dropbox applet requires Java 1.x or higher!");
			pnl.setLayout(new BorderLayout());
			lbl.setHorizontalAlignment(JLabel.CENTER);
			lbl.setHorizontalTextPosition(JLabel.CENTER);
			pnl.add(lbl,"Center");
			setContentPane(pnl);
			started = true; // Nothing to do after this...
		}
	}
	public void run() {
		// Give the GUI a second to display...
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		// Call begin with the arguments.
		dropBoxPnl.begin(args);
	}
	public void setSize(int w, int h) {
		super.setSize(w,h);
		validate();
	}
	public void setTitle(String title) {
		if (eval == null) {
			System.out.println("SetTitle: no access to JavaScript!");
			return;
		}
		try {
			Object[] evalParms = { "self.document.title = \"" + title + "\";" };
			eval.invoke(jsWindow,evalParms);
		}
		catch (IllegalAccessException iae) {
			System.out.println("Illegal access exception: " + iae.getMessage() + "\n");
		}
		catch (IllegalArgumentException iarge) {
			System.out.println("Illegal argument exception: " + iarge.getMessage() + "\n");
		}
		catch (InvocationTargetException ite) {
			System.out.println("Invocation target exception: " + ite.getMessage() + "\n");
		}
	}
	public void start() {
		if (! started) {
			initDlg = new AboutWindow(owner);

			initDlg.setLocationRelativeTo(owner);
			SwingUtilities.invokeLater(new MethodRunner(initDlg,"showAbout"));
			initDlgShown = System.currentTimeMillis();

			// Call begin on a separate thread (allows applet to render itself).
			WorkerThread t = new WorkerThread(this);
			t.start();
		}
	}
	public void stop() {
		dropBoxPnl.ownerEvent(DropBoxPnl.WINDOW_CLOSING);
	}
	public void destroy() {
		// Wipe out the DropBoxPnl?
	}
}
