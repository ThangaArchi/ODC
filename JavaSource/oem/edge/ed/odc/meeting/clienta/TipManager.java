package oem.edge.ed.odc.meeting.clienta;

/**
 * Insert the type's description here.
 * Creation date: (07/26/00 11:31:52 AM)
 * @author: Michael Zarnick
 */
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class TipManager extends MouseAdapter implements MouseMotionListener, Runnable {
	private static TipManager sharedInstance = null;
	private static Color tipColor = new Color(255,255,225);
	private Popup tipWindow = null;
	private Thread timer = null;
	private Component currentObject = null;
	private Point currentP = new Point(0,0);
	private long doShow = 0;
	private long doHide = 0;
	private Hashtable objects = new Hashtable();

	// HEAVY
	private class Popup extends Window {
		boolean  firstShow = true;
		String tip;

		public Popup(Frame f, String t) {
			super(f);
			this.tip = t;
		}

		public void show(int x, int y) {
			this.setLocation(x,y);
			this.setVisible(true);

			/** This hack is to workaround a bug on Solaris where the windows does not really show
			  *  the first time
			  *  It causes a side effect of MS JVM reporting IllegalArumentException: null source
			  *  fairly frequently - also happens if you use HeavyWeight JPopup, ie JComboBox 
			  */
			if(firstShow) {
				this.hidePopup();
				this.setVisible(true);
				firstShow = false;
			}
		}

		public void hidePopup() {
			super.setVisible(false);
			/** We need to call removeNotify() here because hide() does something only if
			  *  Component.visible is true. When the app frame is miniaturized, the parent 
			  *  frame of this frame is invisible, causing AWT to believe that this frame
			  *  is invisible and causing hide() to do nothing
			  */
			removeNotify();
		}

		public void paint(Graphics g) {
			g.setColor(Color.black);
			Dimension s = getSize();
			g.drawRect(0,0,s.width-1,s.height-1);
			g.setColor(tipColor);
			g.fillRect(1,1,s.width-2,s.height-2);
			FontMetrics fm = g.getFontMetrics();
			g.setColor(Color.black);
			g.drawString(tip,2,2+fm.getHeight()-fm.getDescent());
		}
	}

	private boolean tooltipEnabled;
	private boolean tipShowing = false;
	private boolean adjusting = false;
/**
 * HelpTipManager constructor comment.
 */
public TipManager() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2002 9:58:53 AM)
 * @param t TipOwner
 */
public void add(Component c, String tip) {
	if (tip != null && tip.trim().length() > 0) {
		objects.put(c,tip);
		c.addMouseListener(this);
		c.addMouseMotionListener(this);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2002 9:58:53 AM)
 * @param t TipOwner
 */
public void add(TipOwner t) {
	if (t instanceof Component) {
		Component c = (Component) t;
		c.addMouseListener(this);
		c.addMouseMotionListener(this);
	}
}
static Frame frameForComponent(Component component) {
	while (!(component instanceof Frame)) {
		component = component.getParent();
	}
	return (Frame)component;
}
/**
 * Insert the method's description here.
 * Creation date: (07/26/00 11:33:41 AM)
 * @return com.ibm.ere.client.HelpTipManager
 */
public static TipManager getTipManager() {
	if (sharedInstance == null) {
		sharedInstance = new TipManager();
	}

	return sharedInstance;
}
synchronized public void hideTipWindow() {
	if (tipWindow != null) {
		tipWindow.hidePopup();
		tipWindow = null;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2002 10:08:06 AM)
 * @param e java.awt.event.MouseEvent
 */
public void mouseDragged(MouseEvent e) {}
public void mouseEntered(MouseEvent e) {
	currentObject = (Component) e.getSource();
	currentP.x = e.getX();
	currentP.y = e.getY();
	timer = new Thread(this);
	doShow = System.currentTimeMillis();
	//timer.start();
}
public void mouseExited(MouseEvent e) {
	timer = null;
	hideTipWindow();
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2002 10:08:06 AM)
 * @param e java.awt.event.MouseEvent
 */
public void mouseMoved(MouseEvent e) {
	currentP.x = e.getX();
	currentP.y = e.getY();
	//doShow = System.currentTimeMillis();
}
public void mousePressed(MouseEvent event) {
	timer = null;
	hideTipWindow();
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2002 9:58:53 AM)
 * @param t TipOwner
 */
public void remove(Component c) {
	c.removeMouseListener(this);
	c.removeMouseMotionListener(this);
	objects.remove(c);
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2002 9:58:53 AM)
 * @param t TipOwner
 */
public void remove(TipOwner t) {
	if (t instanceof Component) {
		Component c = (Component) t;
		c.removeMouseListener(this);
		c.removeMouseMotionListener(this);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2002 10:18:00 AM)
 */
public void run() {
	try {
		boolean done = false;
		while (! done) {
			// Sleep so we wake up 1 second past doShow.
			Thread.currentThread().sleep(1000 - (System.currentTimeMillis() - doShow));

			if (timer != Thread.currentThread())
				return;

			// If 1 second has passed since doShow.
			if (System.currentTimeMillis() - doShow > 999) {
				showTipWindow();
				done = true;
			}
		}

		done = false;
		doHide = System.currentTimeMillis();
		while (! done) {
			// Sleep so we wake up 3 seconds past doHide.
			Thread.currentThread().sleep(3000 - (System.currentTimeMillis() - doHide));

			if (timer != Thread.currentThread())
				return;

			// If 3 seconds has passed since doHide.
			if (System.currentTimeMillis() - doHide > 2999) {
				hideTipWindow();
				done = true;
			}
		}
	}
	catch (InterruptedException e) {}
}
synchronized public void showTipWindow() {
	String tip;
	if (currentObject instanceof TipOwner)
		tip = ((TipOwner) currentObject).getTipText();
	else
		tip = (String) objects.get(currentObject);

	Point screenLocation = currentObject.getLocationOnScreen();
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	Point location = new Point();

	// Find the current object's frame.
	Component c = currentObject;
	while (c != null && ! (c instanceof Frame))
		c = (Container)c.getParent();

	tipWindow = new Popup((Frame) c,tip);

	FontMetrics fm = tipWindow.getFontMetrics(tipWindow.getFont());
	int w = fm.stringWidth(tip);

	Dimension size = new Dimension(fm.stringWidth(tip)+4,fm.getHeight()+4);
	tipWindow.setSize(size);

	// Position the tip window below and slightly to the right of the component.
	location.x = screenLocation.x + currentP.x + 5;
	location.y = screenLocation.y + currentP.y + 5;

	// If the tip window is off the right edge of the screen, move it to the left
	// to keep it on the screen.
	if (location.x + size.width > screenSize.width) {
		location.x -= location.x + size.width - screenSize.width;
	}

	// If the tip window is off the left edge of the screen, start it on the left edge.
	if (location.x < 0) {
		location.x = 0;
	}

	// If the tip window is off the bottom, move it up on the component.
	if (location.y + size.height > screenSize.height) {
		location.y -= location.y + size.height - screenSize.height;
	}

	// If the help window is off the top, then that's a big window!
	if (location.y < 0) {
		location.y = 0;
	}

	tipWindow.show(location.x,location.y);
}
}
