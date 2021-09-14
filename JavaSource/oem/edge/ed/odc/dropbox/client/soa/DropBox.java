/*
 * Created on Feb 7, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.dropbox.client.soa;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import com.ibm.as400.webaccess.common.ConfigFile;
import com.ibm.as400.webaccess.common.ConfigSection;

/**
 * @author zarnick
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DropBox extends JFrame implements DropBoxPnlListener {
	public static JFrame initDlg;
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
			initDlg = new JFrame("Drop Box");
			initDlg.getContentPane().setLayout(new BorderLayout());
			initLbl = new JLabel("Initializing...");
			initLbl.setHorizontalAlignment(JLabel.CENTER);
			initLbl.setHorizontalTextPosition(JLabel.CENTER);
			initDlg.getContentPane().add(initLbl,"Center");
			Dimension s = new Dimension(200,100);
			initDlg.pack();
			initDlg.setSize(new java.awt.Dimension(201,110));

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
			SwingUtilities.invokeLater(new MethodRunner(initDlg,"show"));

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
		else if (e.isReadyToShow()) {
			// Hide the initializing dialog.
			SwingUtilities.invokeLater(new MethodRunner(initDlg,"hide"));
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
