package oem.edge.ed.odc.applet;
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
 * Creation date: (03/07/01 1:16:02 PM)
 * @author: Michael Zarnick
 */
import java.awt.*;
import java.awt.event.*;
import java.net.*;

public class About extends Window {
	private AboutPnl ivjAboutPnl1 = null;
/**
 * About constructor comment.
 * @param owner java.awt.Frame
 */
public About(java.awt.Frame owner) {
	super(owner);
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (03/07/01 12:14:21 PM)
 */
public void display() {
	// Center the window
	Dimension screen = Toolkit.getDefaultToolkit ().getScreenSize();
	Rectangle win = getBounds();
	setLocation((screen.width - win.width) / 2,(screen.height - win.height) / 2);
	setVisible(true);
}
/**
 * Return the AboutPnl1 property value.
 * @return oem.edge.ed.odc.applet.AboutPnl
 */
private AboutPnl getAboutPnl1() {
	if (ivjAboutPnl1 == null) {
		try {
			ivjAboutPnl1 = new oem.edge.ed.odc.applet.AboutPnl();
			ivjAboutPnl1.setName("AboutPnl1");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjAboutPnl1;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	//System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	//exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("About");
		setLayout(new java.awt.BorderLayout());
		setSize(450, 350);
		add(getAboutPnl1(), "Center");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (03/07/01 12:14:21 PM)
 */
public void invisible(long delay) {
	try {
		Thread.sleep(delay);
	}
	catch (Exception e) {
		System.out.println(e);
	}
	setVisible(false);
	dispose();
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		About aAbout = new oem.edge.ed.odc.applet.About(new java.awt.Frame());
		aAbout.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aAbout.show();
		java.awt.Insets insets = aAbout.getInsets();
		aAbout.setSize(aAbout.getWidth() + insets.left + insets.right, aAbout.getHeight() + insets.top + insets.bottom);
		aAbout.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Window");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/2/2001 12:04:16 PM)
 * @param name java.lang.String
 */
public void setCompName(String name) {
	getAboutPnl1().setCompName(name);
}
}  // @jve:visual-info  decl-index=0 visual-constraint="20,20"
