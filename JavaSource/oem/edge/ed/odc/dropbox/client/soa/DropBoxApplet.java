/*
 * Created on Feb 7, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.dropbox.client.soa;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.*;
import java.net.URL;

import javax.swing.*;

/**
 * @author zarnick
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DropBoxApplet extends JApplet implements DropBoxPnlListener, Runnable {
	private DropBoxPnl dropBoxPnl;
	private JDialog initDlg = null;
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
			Object[] evalParms = { "self.close();" };
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
		else if (e.isReadyToShow()) {
			// We're an applet, we're already showing. Can try to use
			// JavaScript to move and size the window though (if we can
			// figure out how to use it to get the size and position).
			SwingUtilities.invokeLater(new MethodRunner(initDlg,"hide"));
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
				int i = 2; // For url.
				if (token != null) i += 2;
				args = new String[i];
				args[0] = "-URL";
				args[1] = url;
				if (token != null) {
					args[2] = "-CH_TOKEN";
					args[3] = token;
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
			initDlg = new JDialog(owner,"Drop Box");
			initDlg.getContentPane().setLayout(new BorderLayout());
			JLabel initLbl = new JLabel("Initializing...");
			initLbl.setHorizontalAlignment(JLabel.CENTER);
			initLbl.setHorizontalTextPosition(JLabel.CENTER);
			initDlg.getContentPane().add(initLbl,"Center");
			initDlg.pack();
			initDlg.setSize(new java.awt.Dimension(201,110));

			initDlg.setLocationRelativeTo(owner);
			SwingUtilities.invokeLater(new MethodRunner(initDlg,"show"));

			// Call begin on a separate thread (allows applet to render itself).
			WorkerThread t = new WorkerThread(this);
			t.start();
		}
	}
	public void stop() {
		// Need to drive DropBoxPnl to quiesce all activity, logoff, and exit...
	}
	public void destroy() {
		// Wipe out the DropBoxPnl?
	}
}
