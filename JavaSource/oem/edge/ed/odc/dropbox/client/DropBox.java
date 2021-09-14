package oem.edge.ed.odc.dropbox.client;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import oem.edge.ed.odc.dsmp.client.AboutWindow;

import com.ibm.as400.webaccess.common.ConfigFile;
import com.ibm.as400.webaccess.common.ConfigSection;
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

public class DropBox extends JFrame implements DropBoxPnlListener {
	public static AboutWindow initDlg;
	public static long initDlgShown;
	public static JLabel initLbl;

	private DropBoxPnl dropBoxPnl;
	private ConfigFile dboxini = null;

	/**
	 * @throws java.awt.HeadlessException
	 */
	public DropBox() {
		super();
		initialize();
	}

	public static void main(String[] args) {
		try {
			initDlg = new AboutWindow();
			initDlg.setModal(true);
			initDlg.setPage(initDlg.getClass().getResource("/about.html"));
			Dimension s = initDlg.getSize();

			Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();

			int x = (scrSize.width - s.width) / 2;
			int y = (scrSize.height - s.height) / 2;

			int dx = x + s.width - scrSize.width;
			if (dx > 0)
				x -= dx;

			if (x < 0)
				x = 0;

			int dy = y + s.height - scrSize.height;
			if (dy > 0)
				y -= dy;

			if (y < 0)
				y = 0;

			initDlg.setLocation(x,y);
			SwingUtilities.invokeLater(new MethodRunner(initDlg,"showAbout"));
			initDlgShown = System.currentTimeMillis();

			DropBox aDropBox = new DropBox();
			aDropBox.begin(args);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of DropBoxFrame");
			exception.printStackTrace(System.out);
		}
	}

	private void begin(String[] args) {
		try	{
			File temp = new File("dropbox.ini");
			if (temp.exists()) {
				dboxini.load("dropbox.ini");
			}
		}
		catch (Exception e) {
			System.out.println("Exception in loading dropbox.ini");
		}

		dropBoxPnl.begin(args);
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

			// Hide the initializing dialog.
			SwingUtilities.invokeLater(new MethodRunner(initDlg,"hideAbout"));
		}
		else if (e.isShow()) {
			// Set the size and location.
			SwingUtilities.invokeLater(new MethodRunner(this,"loadPreferences"));
			// Show the main window.
			SwingUtilities.invokeLater(new MethodRunner(this,"show"));
		}
		else if (e.isSavePreferences()) {
			savePreferences();
		}
		else if (e.isExit()) {
			System.exit(0);
		}
	}
	private void handleException(java.lang.Throwable exception) {
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		exception.printStackTrace(System.out);
	}
	private void initialize() {
		setName("DropBox");
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle("Drop Box");
		setSize(632, 553);
		
		dropBoxPnl = new DropBoxPnl(this,getRootPane());
		dropBoxPnl.addDropBoxPnlListener(this);
		setJMenuBar(dropBoxPnl.getDropBoxJMenuBar());
		setContentPane(dropBoxPnl);

		this.addWindowListener(new java.awt.event.WindowAdapter() {   
			public void windowOpened(java.awt.event.WindowEvent e) {    
				try {
					dropBoxPnl.ownerEvent(DropBoxPnl.WINDOW_OPEN);
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			} 
			public void windowClosing(java.awt.event.WindowEvent e) {    
				try {
					dropBoxPnl.ownerEvent(DropBoxPnl.WINDOW_CLOSING);
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			}
		});

		dboxini = new ConfigFile();
	}
	public void loadPreferences() {
		// Restore window size, if possible.
		Vector v = dboxini.getSection("WindowState");
		if (v != null && v.size() == 1) {
			Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();

			ConfigSection wp = (ConfigSection) v.elementAt(0);
			int x = wp.getIntProperty("xLoc",-1);
			int y = wp.getIntProperty("yLoc",-1);
			int w = wp.getIntProperty("width",-1);
			int h = wp.getIntProperty("height",-1);

			if (w > -1 && h > -1) {
				if (w > scrSize.width) w = scrSize.width;
				if (h > scrSize.height) h = scrSize.height;
				setSize(w,h);
			}

			Dimension winSize = getSize();

			if (x > -1 && y > -1) {
				if (x + winSize.width > scrSize.width) x = scrSize.width - winSize.width;
				if (y + winSize.height > scrSize.height) y = scrSize.height - winSize.height;

				setLocation(x,y);
			}
			else {
				x = (scrSize.width - winSize.width) / 2;
				y = (scrSize.height - winSize.height) / 2;

				int dx = x + winSize.width - scrSize.width;
				if (dx > 0)
					x -= dx;

				if (x < 0)
					x = 0;

				int dy = y + winSize.height - scrSize.height;
				if (dy > 0)
					y -= dy;

				if (y < 0)
					y = 0;

				setLocation(x,y);
			}
		}
		// Otherwise, use the default.
		else {
			setSize(800,550);
	
			Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension winSize = getSize();
	
			int x = (scrSize.width - winSize.width) / 2;
			int y = (scrSize.height - winSize.height) / 2;
	
			int dx = x + winSize.width - scrSize.width;
			if (dx > 0)
				x -= dx;
	
			if (x < 0)
				x = 0;
	
			int dy = y + winSize.height - scrSize.height;
			if (dy > 0)
				y -= dy;
	
			if (y < 0)
				y = 0;
	
			setLocation(x,y);
		}
	}
	public void savePreferences() {
		// Need to reload the preferences file.
		try	{
			File temp = new File("dropbox.ini");
			if (temp.exists()) {
				dboxini.load("dropbox.ini");
			}
			else {
				return;
			}
		}
		catch (Exception e) {
			System.out.println("Exception in loading dropbox.ini");
			return;
		}

		// Save window size.
		Point sl = getLocationOnScreen();
		Dimension sz = getSize();
		ConfigSection wp = new ConfigSection("WindowState");
		wp.setIntProperty("xLoc",sl.x);
		wp.setIntProperty("yLoc",sl.y);
		wp.setIntProperty("width",sz.width);
		wp.setIntProperty("height",sz.height);
		dboxini.removeSection(wp.getName());
		dboxini.addSection(wp);

		// Save ini file to disk.
		try {
			dboxini.store("dropbox.ini");
		}
		catch (IOException e) {
			System.out.println("Unable to save preferences as dropbox.ini");
		}
	}
}
