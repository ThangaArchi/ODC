package oem.edge.ed.odc.meeting.client;

import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.*;
import java.util.*;
import oem.edge.ed.odc.meeting.common.*;
/**
 * Insert the type's description here.
 * Creation date: (3/21/2003 3:17:06 PM)
 * @author: Mike Zarnick
 */
public class ImagePanel extends JPanel implements MeetingListener, ImageListener, Runnable {
	private class Pointer {
		int x;
		int y;
		int width;
		int height;
		String name;

		Pointer(int x, int y, int w, int h, String name) {
			this.x = x;
			this.y = y;
			width = w;
			height = h;
			this.name = name;
		}
	}

	public static int NORMAL_KEY = 0;
	public static int MIXED_KEY = 1;
	public static int STRICT_KEY = 2;
	private int imageWidth = 0;
	private int imageHeight = 0;
	private int bw = 64;
	private int bh = 64;
	private Image image = null;
	private Graphics imageGraphics = null;
	private Image cursor = null;
	private Graphics cursorGraphics = null;
	private Rectangle ir = null;
	private boolean inControl = false;
	private boolean inTelepoint = false;
	private DSMPDispatcher dispatcher = null;
	private BScraper bscraper = null;
	private Thread scrapeThread = null;
	private Thread cursorThread = null;
	private Thread redrawThread = null;
	private Thread resizeThread = null;
	private boolean needFullImage = false;
	private boolean doSendTime = false;
	private boolean compress = true;
	private boolean drawgrid = false;
	private boolean fitImage = false;
	private boolean autoResize = true;
	private JFrame parentFrame = null;
	private int keyEventMode = 0;
	private double fitScale = 1.0;
	private long receiveStartTime = -1;
	private long receiveEndTime = -1;
	private long lastDraw = -1;
	private Point mousePosition = null;
	private Hashtable pointers = new Hashtable();
	private Color mouseRed = new Color(128,0,0);
	private Color focusColor = javax.swing.plaf.metal.MetalLookAndFeel.getFocusColor();
	private Byte Pause = new Byte((byte) 0);
	private boolean pause = false;
	private PresencePanel presencePanel = null;
/**
 * ImagePanel constructor comment.
 */
public ImagePanel() {
	super();
	enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
}
/**
 * ImagePanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ImagePanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * ImagePanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ImagePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * ImagePanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ImagePanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Insert the method's description here.
 * Creation date: (8/27/2002 11:21:13 AM)
 * @return java.awt.Image
 * @param c java.awt.Color
 */
private void drawCursorImage(Graphics g,int X, int Y, Color c) {
	// int x[] = { 0,4,7,13,15,9,15 };
	// int y[] = { 0,15,9,15,13,7,4 };
	int x[] = new int[7];
	x[0] = X+0;
	x[1] = X+4;
	x[2] = X+7;
	x[3] = X+13;
	x[4] = X+15;
	x[5] = X+9;
	x[6] = X+15;
	int y[] = new int[7];
	y[0] = Y+0;
	y[1] = Y+15;
	y[2] = Y+9;
	y[3] = Y+15;
	y[4] = Y+13;
	y[5] = Y+7;
	y[6] = Y+4;

	g.setColor(c);
	g.fillPolygon(x,y,x.length);
	g.setColor(Color.black);
	g.drawPolygon(x,y,x.length);
}
/**
 * Insert the method's description here.
 * Creation date: (8/27/2002 11:21:13 AM)
 * @return java.awt.Image
 * @param c java.awt.Color
 */
private void drawPointerImage(Graphics g,int X,int Y,String name,int nwidth,Color c) {
	// int x[] = { 0,4,7,13,15,9,15 };
	// int y[] = { 0,15,9,15,13,7,4 };
	int x[] = new int[7];
	int DX = X+2;
	x[0] = DX+0;
	x[1] = DX+4;
	x[2] = DX+7;
	x[3] = DX+13;
	x[4] = DX+15;
	x[5] = DX+9;
	x[6] = DX+15;
	int y[] = new int[7];
	int DY = Y+2;
	y[0] = DY+0;
	y[1] = DY+15;
	y[2] = DY+9;
	y[3] = DY+15;
	y[4] = DY+13;
	y[5] = DY+7;
	y[6] = DY+4;

	g.setColor(Color.black);
	g.drawRect(X,Y,19,19);
	FontMetrics fm = g.getFontMetrics();
	g.setColor(c);
	//g.fillRect(X+21,Y+20-fm.getHeight(),g.getClipBounds().width,fm.getHeight());
	g.fillRect(X+21,Y+20-fm.getHeight(),nwidth-22,fm.getHeight());
	g.setColor(Color.black);
	g.drawString(name,X+22,Y+20-fm.getDescent());
	//g.translate(2,2);
	g.setColor(c);
	g.fillPolygon(x,y,x.length);
	g.setColor(Color.black);
	g.drawPolygon(x,y,x.length);
}
/**
 * Insert the method's description here.
 * Creation date: (7/21/2002 3:28:21 PM)
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	if (fitImage)
		return new Dimension(0,0);
	else
		return new Dimension(imageWidth+6,imageHeight+6);
}
/**
 * Insert the method's description here.
 * Creation date: (4/15/2003 2:49:30 PM)
 * @return int
 * @param e java.awt.event.MouseEvent
 */
private int getMouseButton(MouseEvent e) {
	if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
		return 1;
	else if ((e.getModifiers() & MouseEvent.BUTTON2_MASK) != 0)
		return 2;
	else
		return 3;
}
/**
 * Insert the method's description here.
 * Creation date: (12/6/2004 10:07:12 AM)
 * @return javax.swing.JFrame
 */
public JFrame getParentFrame() {
	return parentFrame;
}
/**
 * Insert the method's description here.
 * Creation date: (7/21/2002 3:28:21 PM)
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return getMinimumSize();
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/2002 2:01:55 PM)
 * @param p PresencePanel
 */
public PresencePanel getPresencePanel() {
	return presencePanel;
}
/**
 * Insert the method's description here.
 * Creation date: (8/8/2002 2:23:01 PM)
 * @return long
 */
public long getReceiveTime() {
	if (receiveStartTime == -1)
		return -1;

	long delta = (receiveStartTime == -2) ? 0 : receiveEndTime - receiveStartTime;
	receiveStartTime = -1;

	return delta;
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 11:44:31 AM)
 * @param e ImageEvent
 */
public void imageChanged(ImageEvent e) {
	if (e.isResize())
		setImageSize(e.width,e.height);
	else if (e.isUpdate()) {
		/*
		int[] pix = new int[e.width*e.height];
		int j = 0;
		e.length += e.offset;
		for (int i = e.offset; i < e.length; i++)
			pix[j++] = (e.pixels[i++] << 24 & 0xff000000) + (e.pixels[i++] << 16 & 0xff0000) +
					(e.pixels[i++] << 8 & 0xff00) + (e.pixels[i] & 0xff);
		*/
		int pix[] = BScraper.runlengthDecode(e.pixels,null,e.offset,e.length,e.width*e.height);
		setImage(e.x,e.y,e.width,e.height,pix);
	}
	else if (e.isUpdateEnd())
		setImage(0,0,0,0,null);
	else if (e.isMouse()) {
		if (e.isMouseReal()) {
			//System.out.println("real");
			if (bscraper != null)
				bscraper.injectMouse(new Point(e.x,e.y),e.isMousePress(),e.button);
			else
				moveMouse(e.x,e.y);
		}
		else {
			movePointer(e.participantID,e.x,e.y);
		}
	}
	else if (e.isKey()) {
		if (bscraper != null) {
			bscraper.injectKey(bscraper.getCursorPosition(),e.isKeyPress(),e.isKeyCode(),e.key);
		}
	}
	else if (e.isKeyFailed() || e.isMouseFailed() || e. isResizeFailed() || e.isUpdateFailed()) {
		System.out.println("ImagePnl.imageChanged: protocol error: " + e.message);
	}
	else if (e.isFullImage()) {
		synchronized (this) {
			needFullImage = true;
		}
	}
	else
		System.out.println("ImagePnl.imageChanged: unknown ImageEvent type = " + e.reason);
}
/**
 * Insert the method's description here.
 * Creation date: (4/15/2003 2:20:46 PM)
 * @return boolean
 */
public boolean isFocusTraversable() {
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (4/15/2003 2:24:14 PM)
 * @return boolean
 */
public boolean isManagingFocus() {
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 9:59:52 AM)
 */
public boolean isPaused() {
	return pause;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 9:59:52 AM)
 */
public boolean isSharing() {
	return scrapeThread != null;
}
/**
 * ImagePanel constructor comment.
 */
private void layoutImage() {
	// If we are not fitting the image to the allotted space,
	// we need to either resize the window or resize the image
	// or both.
	if (! fitImage) {
		// Set our panel size to be image + room for focus border.
		setSize(imageWidth+6,imageHeight+6);

		if (autoResize) {
			// Get relevant sizes and locations.
			Dimension fs = parentFrame.getSize();
			Point fl = parentFrame.getLocationOnScreen();
			Dimension mbs = parentFrame.getJMenuBar().getSize();
			Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
			Insets fin = parentFrame.getInsets();

			/*
			System.out.println("Frame size: " + fs);
			System.out.println("Frame insets: " + fin);
			System.out.println("Frame menu size: " + mbs);
			System.out.println("Frame location: " + fl);
			System.out.println("Screen size: " + scr);
			System.out.println("Image size: " + (imageWidth+6) + "x" + (imageHeight+6));
			*/

			// Set new size of window. This is the delta of the image
			// and scrollpane sizes. We need to account for a 3 pixel
			// border around the image and a 1 pixel border around the
			// viewport. So, net: image is actually 9 pixels bigger than
			// its height and width.
			fs.width = imageWidth + 9 + fin.left + fin.right;
			fs.height = imageHeight + 9 + fin.top + mbs.height + fin.bottom;

			// Adjust window size and location to stay on screen.

			// Window wider than screen?
			if (fs.width > scr.width) {
				fs.width = scr.width;
			}

			// Window off screen to right?
			if (fl.x + fs.width > scr.width) {
				fl.x = scr.width - fs.width;
			}

			// Window taller than screen?
			if (fs.height > scr.height) {
				fs.height = scr.height;
			}

			// Window off screen bottom?
			if (fl.y + fs.height > scr.height) {
				fl.y = scr.height - fs.height;
			}

			// System.out.println("Change window size to: " + fs);
			parentFrame.setLocation(fl);
			parentFrame.setSize(fs);
		}

		// Redo the layout.
		invalidate();
		parentFrame.validate();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/5/2002 6:00:18 PM)
 * @param e MeetingEvent
 */
public void meetingAction(MeetingEvent e) {
	// Transfer of control
	if (e.isControlEvent()) {
		// Meeting owner never gets control (in this sense).
		if (! dispatcher.isModerator) {
			if (e.participantID == dispatcher.particpantID)
				startControl();
			else if (inControl)
				stopControl();
		}
	}
	else if (e.isStopShareEvent()) {
		imageWidth = imageHeight = 0;
		repaint();
	}
	else if (e.isFreeze()) {
		startFreeze();
	}
	else if (e.isThaw()) {
		stopFreeze();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/27/2002 10:25:08 AM)
 * @param x int
 * @param y int
 */
public void moveMouse(int x, int y) {
	if (mousePosition == null || mousePosition.x != x || mousePosition.y != y) {
		Point oldMousePosition = mousePosition;
		mousePosition = new Point(x,y);
		if (oldMousePosition != null)
			repaint(oldMousePosition.x+3,oldMousePosition.y+3,16,16);
		repaint(mousePosition.x+3,mousePosition.y+3,16,16);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/27/2002 10:25:08 AM)
 * @param pid int
 * @param x int
 * @param y int
 */
public void movePointer(int pid, int x, int y) {
	Integer pID = new Integer(pid);
	Pointer p = (Pointer) pointers.get(pID);
	if (p == null || p.x+2 != x || p.y+2 != y) {
		if (p == null) {
			FontMetrics fm = getFontMetrics(getFont());
			String name = presencePanel.getDataModel().getNickName(pid);
			p = new Pointer(x-2,y-2,22+fm.stringWidth(name),Math.max(20,fm.getHeight()),name);
			pointers.put(pID,p);
		}
		else {
			int oldx = p.x;
			int oldy = p.y;
			p.x = x-2;
			p.y = y-2;
			repaint(oldx+3,oldy+3,p.width,p.height);
		}
		repaint(p.x+3,p.y+3,p.width,p.height);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2003 4:04:57 PM)
 * @param g java.awt.Graphics
 */
public void paint(Graphics g) {
	Dimension s = getSize();
	Rectangle b = g.getClipBounds();

	boolean scale = fitImage && ((s.width-6) < imageWidth || (s.height-6) < imageHeight);

	int sw = imageWidth;
	int sh = imageHeight;

	if (fitImage) {
		sw = (int) (fitScale * sw);
		sh = (int) (fitScale * sh);
	}

	int fw = sw+6;
	int fh = sh+6;
	//System.out.println("Update: " + b.x + "," + b.y + "," + b.width + "," + b.height);

	if (image != null && b.x < fw && b.y < fh) {
		// Handle the focus border.
		g.setColor(hasFocus() ? focusColor : getBackground());
		g.drawRect(1,1,fw-2,fh-2);
		g.setColor(getBackground());
		g.drawRect(0,0,fw,fh);
		g.drawRect(2,2,fw-4,fh-4);

		// Now draw the image.
		synchronized (image) {
			g.drawImage(image,3,3,sw,sh,null);
		}

		// If we are in telepoint mode, draw the individual cursors.
		if (inTelepoint) {
			int cbr = b.x + b.width;
			int cbb = b.y + b.height;
			int x = 0;
			int y = 0;
			synchronized (pointers) {
				Enumeration keys = pointers.keys();
				while (keys.hasMoreElements()) {
					Integer pid = (Integer) keys.nextElement();
					Pointer p = (Pointer) pointers.get(pid);
					if (p != null) {
						if (fitImage) {
							x = (int) (fitScale * p.x);
							y = (int) (fitScale * p.y);
						}
						else {
							x = p.x;
							y = p.y;
						}
						x+=3;
						y+=3;
						if (x < cbr && x + p.width > b.x && y < cbb && y + p.height > b.y)
							drawPointerImage(g,x,y,p.name,p.width,Color.white);
					}
				}
			}
		}
		// Normal mode, draw the mouse, if present
		else if (mousePosition != null) {
			int x = 3;
			int y = 3;
			if (fitImage) {
				x += (int) (fitScale * mousePosition.x);
				y += (int) (fitScale * mousePosition.y);
			}
			else {
				x += mousePosition.x;
				y += mousePosition.y;
			}
			drawCursorImage(g,x,y,mouseRed);
		}
	}

	if (fw < s.width) {
		g.clearRect(fw,0,s.width-fw,fh);
	}
	if (fh < s.height) {
		g.clearRect(0,fh,s.width,s.height-fh);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/2002 3:23:51 PM)
 */
public void pause() {
	synchronized (Pause) {
		if (pause)
			return;

		pause = true;

		try {
			Pause.wait();
		}
		catch (InterruptedException e) {}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/15/2003 1:01:24 PM)
 * @param e java.awt.event.KeyEvent
 */
protected void processComponentKeyEvent(KeyEvent e) {
	// If we are in control of the meeting, then we need to
	// capture the key event and foreward it to the meeting
	// owner for processing.
	if (inControl) {
		if (e.getID() == KeyEvent.KEY_PRESSED || e.getID() == KeyEvent.KEY_RELEASED) {
			int c = (int) e.getKeyChar();
			boolean keyCode = false;
			boolean pressed = e.getID() == KeyEvent.KEY_PRESSED;

			// If normal key event mode and keyChar is undefined, send
			// keyCode instead.
			if ((keyEventMode == NORMAL_KEY && (c == 0x0ffff || c == 0x0)) ||
				(keyEventMode == MIXED_KEY && (c < 20 || c > 127)) ||
				keyEventMode == STRICT_KEY) {
				c = e.getKeyCode();
				keyCode = true;
			}

			// System.out.println("Key press: " + c + " " + keyCode);
			DSMPProto p = DSMPGenerator.keyUpdate((byte) 0,pressed,keyCode,dispatcher.meetingID,0,0,c);
			dispatcher.dispatchProtocol(p);
		}

		// Consume the key event.
		e.consume();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/16/2003 10:06:37 AM)
 * @param e java.awt.event.FocusEvent
 */
public void processFocusEvent(FocusEvent e) {
	super.processFocusEvent(e);
	repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (4/15/2003 1:01:24 PM)
 * @param e java.awt.event.KeyEvent
 */
protected void processKeyEvent(KeyEvent e) {
	// Need to watch for ctrl-tab when we are in control. This will cause focus to be transferred.
	// In this case, the tab key release and ctrl release are never seen by us. Need to send the
	// ctrl release to the client being controlled.
	if (inControl) {
		if ((e.getKeyCode() == KeyEvent.VK_TAB || e.getKeyChar() == '\t') &&
			(e.getModifiers() & KeyEvent.CTRL_MASK) == KeyEvent.CTRL_MASK) {
			DSMPProto p = DSMPGenerator.keyUpdate((byte) 0,false,true,dispatcher.meetingID,0,0,KeyEvent.VK_TAB);
			dispatcher.dispatchProtocol(p);
		}
	}

	super.processKeyEvent(e);
}
/**
 * Insert the method's description here.
 * Creation date: (4/15/2003 2:32:33 PM)
 * @param e java.awt.event.MouseEvent
 */
protected void processMouseEvent(MouseEvent e) {
	if (inControl) {
		if (e.getID() == MouseEvent.MOUSE_ENTERED) {
			requestFocus();
		}
		else if (e.getID() == MouseEvent.MOUSE_PRESSED) {
			DSMPProto p = DSMPGenerator.mouseUpdate((byte) 0,true,true,true,dispatcher.meetingID,scaleLoc(e.getX()),scaleLoc(e.getY()),getMouseButton(e));
			dispatcher.dispatchProtocol(p);
		}
		else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
			DSMPProto p = DSMPGenerator.mouseUpdate((byte) 0,true,false,true,dispatcher.meetingID,scaleLoc(e.getX()),scaleLoc(e.getY()),getMouseButton(e));
			dispatcher.dispatchProtocol(p);
		}
	}
	else {
		if (e.getID() == MouseEvent.MOUSE_PRESSED && ! hasFocus()) {
			requestFocus();
		}
	}

	super.processMouseEvent(e);
}
/**
 * Insert the method's description here.
 * Creation date: (4/15/2003 2:32:33 PM)
 * @param e java.awt.event.MouseEvent
 */
protected void processMouseMotionEvent(MouseEvent e) {
	if (inControl || inTelepoint) {
		DSMPProto p = DSMPGenerator.mouseUpdate((byte) 0,false,false,!inTelepoint,dispatcher.meetingID,scaleLoc(e.getX()),scaleLoc(e.getY()),-1);
		dispatcher.dispatchProtocol(p);
	}

	super.processMouseEvent(e);
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2002 1:01:13 PM)
 * @param width int
 * @param height int
 */
public void reshape(int x, int y, int width, int height) {
	super.reshape(x,y,width,height);

	if (fitImage) {
		fitScale = Math.min(((double) width-6)/((double)imageWidth),((double)height-6)/((double)imageHeight));
		if (fitScale > 1.0)
			fitScale = 1.0;
	}
	else
		fitScale = 1.0;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/2002 3:23:51 PM)
 */
public void resume() {
	synchronized (Pause) {
		if (! pause)
			return;

		/*
		if (pause == 1) {
			try {
				Pause.wait();
			}
			catch (InterruptedException e) {}
		}
		*/

		pause = false;
		bscraper.resume();
		Pause.notifyAll();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 9:51:49 AM)
 */
public void run() {
	if (Thread.currentThread() == scrapeThread) {
		threadScrape();
	}
	
	if (Thread.currentThread() == cursorThread) {
		threadCursor();
	}

	if (Thread.currentThread() == redrawThread) {
		threadRedraw();
	}
	
	if (Thread.currentThread() == resizeThread) {
		threadResize();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/15/2003 2:50:29 PM)
 * @return int
 * @param x int
 */
private int scaleLoc(int x) {
	if (fitImage)
		return (int) (((double) x) / fitScale);
	else
		return x;
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2002 10:02:29 AM)
 * @param b boolean
 */
public void setAutoResizeImage(boolean b) {
	if (autoResize == b)
		return;

	autoResize = b;

	// Adjust image size?
	if (autoResize) {
		resizeThread = new Thread(this);
		resizeThread.start();
		layoutImage();
	}
	else {
		resizeThread = null;
		/*
		synchronized(resizeThread) {
			resizeThread = null;
		}
		*/
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 9:59:52 AM)
 * @param d DSMPDispatcher
 */
public void setBScraper(BScraper b) {
	if (bscraper == b)
		return;

    if (bscraper != null) {
		Thread t;

		try {
			t = scrapeThread;
			scrapeThread = null;
			t.join();
		}
		catch (InterruptedException e) {
			System.out.println("ImagePanel.setBScaper: scrape thread join interrupted!");
		}

		try {
			t = cursorThread;
			cursorThread = null;
			/* 
			 * Call newPosAvailable to cause the cursor thread to wake up.
			 * If there is no mouse movement over the shared area, then 
			 * the cursor thread would be blocked in its call to
			 * getCursorPosition_wait() and the join will take 40 seconds
			 * the complete (timeout in getCursorPosition_wait()).
			 */
			bscraper.newPosAvailable();
			t.join();
		}
		catch (InterruptedException e) {
			System.out.println("ImagePanel.setBScaper: cursor thread join interruped!");
		}
	
		pause = false;
	}

	bscraper = b;

	if (bscraper != null) {
		scrapeThread = new Thread(this);
		scrapeThread.start();
		cursorThread = new Thread(this);
		cursorThread.start();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 9:59:52 AM)
 */
public void setCompress(boolean b) {
	compress = b;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 9:59:52 AM)
 * @param d DSMPDispatcher
 */
public void setDispatcher(DSMPDispatcher d) {
	if (dispatcher == d)
		return;

	if (dispatcher != null) {
		dispatcher.removeImageListener(this);
		dispatcher.removeMeetingListener(this);
		if (inTelepoint)
			stopFreeze();
		if (inControl)
			stopControl();
		//redrawThread = null;
		imageWidth = imageHeight = 0;
		repaint();
	}

	dispatcher = d;

	if (dispatcher != null) {
		//redrawThread =  new Thread(this);
		//redrawThread.start();
		dispatcher.addImageListener(this);
		dispatcher.addMeetingListener(this);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 9:59:52 AM)
 */
public void setDoSendTime(boolean b) {
	doSendTime = b;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 9:59:52 AM)
 */
public void setDrawGrid(boolean b) {
	drawgrid = b;
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2002 10:02:29 AM)
 * @param b boolean
 */
public void setFitImage(boolean b) {
	if (fitImage == b)
		return;

	fitImage = b;

	if (fitImage) {
		invalidate();
		parentFrame.validate();
	}
	else {
		layoutImage();
	}
}
/**
 * ImagePanel constructor comment.
 */
public void setImage(int x, int y, int w, int h, int[] pixels) {
	// If setImageSize has not been called, we ignore image updates. These
	// might arrive when we first join a meeting in progress.
	if (image != null) {
		// Have an active image, did we get any pixels (no means end of changes marker)?
		if (pixels != null) {
			// Timing the image arrival? 
			if (receiveStartTime == -2) receiveStartTime = System.currentTimeMillis();

			// Build a new image block from the pixels.
			MemoryImageSource mis = new MemoryImageSource(w,h,pixels,0,w);
			Image pi = createImage(mis);

			// Paint this image block into the full image.
			synchronized (image) {
				imageGraphics.setClip(x,y,w,h);
				imageGraphics.drawImage(pi,x,y,null);
				if (drawgrid) {
					imageGraphics.drawLine(x,y+bh-1,x+bw-1,y+bh-1);
					imageGraphics.drawLine(x+bw-1,y,x+bw-1,y+bh-1);
				}
			}

			// Record the image receive time.
			receiveEndTime = System.currentTimeMillis();

			// Adjust x, y, w & h if we are scaling the image to fit the
			// scrollpane's viewport as scaling would affect what needs to be
			// redrawn.
			if (fitImage) {
				x = (int) (fitScale * x);
				y = (int) (fitScale * y);
				w = (int) (fitScale * w);
				h = (int) (fitScale * h);
			}

			// Keep a cumulative rectangle of the area needing to be redrawn.
			if (ir == null)
				ir = new Rectangle(x,y,w,h);
			else
				ir = ir.union(new Rectangle(x,y,w,h));

			// We draw at least every 1/4 sec. If we are not timing yet, then
			// start timing that 1/4 sec from now.
			if (lastDraw == -1)
				lastDraw = receiveEndTime;
			else if (receiveEndTime - lastDraw > 250) {
				if (ir != null) {
					repaint(ir.x+3,ir.y+3,ir.width,ir.height);
					ir = null;
				}
				lastDraw = -1;
			}
		}

		// End of changes marker. Redraw the changes now.
		else {
			if (ir != null) {
				repaint(ir.x+3,ir.y+3,ir.width,ir.height);
				ir = null;
			}
			lastDraw = -1;
		}
	}
}
/**
 * ImagePanel constructor comment.
 */
public void setImageSize(int w, int h) {
	try {
		synchronized (resizeThread) {
			// Really changing image sizes?
			if (imageWidth != w || imageHeight != h) {
				// Changing the image size.
				imageWidth = w;
				imageHeight = h;

				// Free the old image and generate a new Image with the new size.
				image = null;
				imageGraphics = null;
				image = createImage(w,h);
				imageGraphics = image.getGraphics();
			}
			// Not really changing image sizes, just have a complete picture coming.
			else {
				// Clear out the old picture.
				imageGraphics.setColor(getBackground());
				imageGraphics.fillRect(0,0,w,h);
				repaint(3,3,w,h);
			}
		}
	}
	catch (Exception e) {
		// No auto resize thread.

		// Really changing image sizes?
		if (imageWidth != w || imageHeight != h) {
			// Changing the image size.
			imageWidth = w;
			imageHeight = h;

			// Free the old image and generate a new Image with the new size.
			image = null;
			imageGraphics = null;
			image = createImage(w,h);
			imageGraphics = image.getGraphics();

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					layoutImage();
				}
			});
		}
		// Not really changing image sizes, just have a complete picture coming.
		else {
			// Clear out the old picture.
			imageGraphics.setColor(getBackground());
			imageGraphics.fillRect(0,0,w,h);
			repaint(3,3,w,h);
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/29/2003 3:08:39 PM)
 * @param mode int
 */
public void setKeyEventMode(int mode) {
	if (mode < NORMAL_KEY || mode > STRICT_KEY)
		return;

	keyEventMode = mode;
}
/**
 * Insert the method's description here.
 * Creation date: (12/6/2004 10:07:12 AM)
 * @param newParentFrame javax.swing.JFrame
 */
public void setParentFrame(JFrame newParentFrame) {
	parentFrame = newParentFrame;
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/2002 2:01:55 PM)
 * @param p PresencePanel
 */
public void setPresencePanel(PresencePanel p) {
	presencePanel = p;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 9:59:52 AM)
 */
private void startControl() {
	// Already in control.
	if (inControl)
		return;

	// Now in control.
	inControl = true;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 9:59:52 AM)
 */
private void startFreeze() {
	// Already in telepoint mode?
	if (inTelepoint)
		return;

	// If we are sharing, then we need to quiesce
	// stop sending out real pointer motions.
	if (bscraper != null)
		cursorThread = null;

	// If we are in control, we shouldn't be.
	if (inControl)
		stopControl();

	// Turn on telepoint mode.
	inTelepoint = true;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 9:59:52 AM)
 */
private void stopControl() {
	// Not in control?
	if (! inControl)
		return;

	// No longer in control.
	inControl = false;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 9:59:52 AM)
 */
private void stopFreeze() {
	// Not in telepoint mode?
	if (! inTelepoint)
		return;

	// Stop Telepointer
	inTelepoint = false;

	// Remove all cursors and repaint their area.
	Enumeration e = pointers.keys();
	while (e.hasMoreElements()) {
		Integer pID = (Integer) e.nextElement();
		Pointer p = (Pointer) pointers.remove(pID);
		repaint(p.x,p.y,p.width,p.height);
	}

	// Resume sending real pointer motions.
	if (bscraper != null) {
		cursorThread = new Thread(this);
		cursorThread.start();
	}
}
/**
 * Cursor thread
 */
private void threadCursor() {
	DSMPProto p = null;

	try {
		// while we are the cursor thread, send out cursor position change updates.
		while (Thread.currentThread() == cursorThread) {
			Point pnt = bscraper.getCursorPostion_Wait();
			p = DSMPGenerator.mouseUpdate((byte) 0,false,false,true,dispatcher.meetingID,pnt.x,pnt.y,-1);
			dispatcher.dispatchProtocol(p);
		}
	}
	catch (NullPointerException e) {
		if (bscraper!= null) {
			System.out.println("ImagePanel.scanCursor(): " + e.getMessage());
			e.printStackTrace();
		}
	}
}
/**
 * Redraw thread
 */
private void threadRedraw() {
	// while we are the redraw thread, redraw every 1/4 second as needed.
	while (Thread.currentThread() == redrawThread) {
		synchronized (redrawThread) {
			if (ir != null) {
				repaint(ir.x+3,ir.y+3,ir.width,ir.height);
				ir = null;
			}
		}

		try {
			Thread.sleep(200);
		}
		catch (Exception e) {
		}
	}
}
/**
 * Redraw thread
 */
private void threadResize() {
	// while we are the resize thread, resize the window every 1 second as needed.
	int lastImageWidth = imageWidth;
	int lastImageHeight = imageHeight;
	while (Thread.currentThread() == resizeThread) {
		synchronized (resizeThread) {
			if (lastImageWidth != imageWidth || lastImageHeight != imageHeight) {
				lastImageWidth = imageWidth;
				lastImageHeight = imageHeight;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						layoutImage();
					}
				});
			}
		}

		try {
			Thread.sleep(2000);
		}
		catch (Exception e) {
		}
	}
}
/**
 * Scraper thread
 * @param b
 */
private void threadScrape() {
	Rectangle currentFrame = null;
	DSMPProto p = null;
	Vector frames = new Vector(10);
	long lastFullScrape = System.currentTimeMillis();
	boolean paused = false;

	try {
		// While we are the scraper thread, scrape the screen.
		while (Thread.currentThread() == scrapeThread) {
			Rectangle nextFrame = null;

			long currentTime = System.currentTimeMillis();
			boolean pausing = false;

			// Need to pause and we are not paused? working on it during this pass.
			if (pause && ! paused)
				pausing = true;

			// Paused and not needed? Unpaused.
			else if (! pause && paused)
				paused = false;

			// Pausing scraping this pass? Need full scrape.
			if (pausing) {
				nextFrame = bscraper.getNewFrame(true);
				if (needFullImage) {
					synchronized(this) {
						needFullImage = false;
					}
				}
				currentFrame = null;
				lastFullScrape = currentTime;
			}

			// Scraping is completely paused?
			else if (paused) {
				// Lock access to resume()
				synchronized(Pause) {
					// Still need to be paused?
					if (pause) {
						// Wait on 15 second boundaries until a new image is need
						// (an arrival) or until we are notified by resume().
						boolean done = false;
						while (! done) {
							// How much time until next boundary?
							long dt = currentTime - lastFullScrape;

							// Need to wait?
							if (dt < 15000) {
								try {
									Pause.wait(15000-dt);
								}
								catch (InterruptedException e) {}
							}

							// Need a full frame (an arrival)?
							if (needFullImage) {
								if (pause)
									nextFrame = bscraper.replayLastFrame();
								else
									nextFrame = bscraper.getNewFrame(true);
								currentFrame = null;
								synchronized(this) {
									needFullImage = false;
								}
								done = true;
							}

							// Not paused any more.
							else if (! pause) {
								nextFrame = bscraper.getNewFrame(false);
								done = true;
							}

							currentTime = lastFullScrape = System.currentTimeMillis();
						}
					}

					// pause changed between "if (! pause && paused)" and "else if (paused)"

					// 15 seconds passed and need a full image?
					else if (currentTime - lastFullScrape > 15000 && needFullImage) {
						nextFrame = bscraper.getNewFrame(true);
						synchronized (this) {
							needFullImage = false;
						}
						currentFrame = null;
						lastFullScrape = currentTime;
					}

					// Just a normal scrape.
					else
						nextFrame = bscraper.getNewFrame(false);
				}
			}

			// Not paused or pausing, 15 seconds passed and need a full image?
			else if (currentTime - lastFullScrape > 15000 && needFullImage) {
				nextFrame = bscraper.getNewFrame(true);
				synchronized (this) {
					needFullImage = false;
				}
				currentFrame = null;
				lastFullScrape = currentTime;
			}

			// Not paused or pausing, just a normal scrape.
			else {
				nextFrame = bscraper.getNewFrame(false);
			}

			// Pausing, pause the unix scraper and tell the user we are
			// woring on it....
			if (pausing) {
				bscraper.pause();
				dispatcher.fireMeetingEvent(new MeetingEvent(MeetingEvent.FREEZING,(byte) 0,(byte) 0));
			}

			// Check to ensure that scraper is still properly configured.
			// (ie. the window we are scraping is still available, app didn't close).
			if (! bscraper.isModeStillValid()) {
				dispatcher.fireStopShare();
				break;
			}

			// Is this scrape a new window size?
			if (nextFrame != null &&
				(currentFrame == null || currentFrame.width != nextFrame.width ||
				 currentFrame.height != nextFrame.height)) {
				currentFrame = nextFrame;
				p = DSMPGenerator.imageResize((byte) 0,dispatcher.meetingID,0,0,nextFrame.width,nextFrame.height);
				dispatcher.dispatchProtocol(p);
				// Pausing, tell ourself.
				if (pausing)
					imageChanged(new ImageEvent(ImageEvent.RESIZE,(byte) 0,(byte) 0,0,0,nextFrame.width,nextFrame.height,null,0,0));
			}

			int totframes = 0;
			//boolean sentFrames = false;
			currentTime = System.currentTimeMillis();
			byte[] pu = null;
			//int i = 0;
			int lastX = 0;
			int lastY = 0;
			while ((pu = bscraper.getUpdatedPixelsInBytes2(pu)) != null) {
				int x = (pu[0] << 24 & 0xff000000) + (pu[1] << 16 & 0xff0000) +
						(pu[2] << 8 & 0xff00) + (pu[3] & 0xff);
				int y = (pu[4] << 24 & 0xff000000) + (pu[5] << 16 & 0xff0000) +
						(pu[6] << 8 & 0xff00) + (pu[7] & 0xff);
				int w = (pu[8] << 24 & 0xff000000) + (pu[9] << 16 & 0xff0000) +
						(pu[10] << 8 & 0xff00) + (pu[11] & 0xff);
				int h = (pu[12] << 24 & 0xff000000) + (pu[13] << 16 & 0xff0000) +
						(pu[14] << 8 & 0xff00) + (pu[15] & 0xff);

				//int len = w * h * 4;
				int len = (pu[17] << 16 & 0xff0000) + (pu[18] << 8  & 0xff00) + (pu[19] & 0xff);

				if ((x <= lastX && y <= lastY) || (x > lastX && y < lastY)) {
					p = DSMPGenerator.frameEnd((byte) 0,dispatcher.meetingID);
					dispatcher.dispatchProtocol(p);

					if (pausing)
						imageChanged(new ImageEvent(ImageEvent.UPDATE_END,(byte) 0,(byte) 0));
				}

				p = DSMPGenerator.frameUpdate((byte) 0,false,dispatcher.meetingID,x,y,w,h,pu,20,len);
				dispatcher.dispatchProtocol(p);

				if (pausing)
					imageChanged(new ImageEvent(ImageEvent.UPDATE,(byte) 0,(byte) 0,x,y,w,h,pu,20,len));

				totframes++;

				lastX = x;
				lastY = y;

				/*
				frames.addElement(p);
				i++;

				if (i == 10) {
					p = DSMPGenerator.multiFrameUpdate((byte) 0,compress,dispatcher.meetingID,frames);
					dispatcher.dispatchProtocol(p);
					sentFrames = true;
					frames.removeAllElements();
					i = 0;
				}
				*/
			}

			/*
			if (i != 0) {
				p = DSMPGenerator.multiFrameUpdate((byte) 0,compress,dispatcher.meetingID,frames);
				dispatcher.dispatchProtocol(p);
				sentFrames = true;
				frames.removeAllElements();
			}
			*/

			if (totframes > 0) {
				p = DSMPGenerator.frameEnd((byte) 0,dispatcher.meetingID);
				dispatcher.dispatchProtocol(p);

				if (pausing)
					imageChanged(new ImageEvent(ImageEvent.UPDATE_END,(byte) 0,(byte) 0));
			}

			//if (doSendTime & sentFrames) {
			if (doSendTime && totframes != 0) {
				long delta = System.currentTimeMillis() - currentTime;
				System.out.println("Sent " + totframes + " frame changes in " + delta + "ms.");
			}

			// Done pausing, we are now paused and the gui can flip.
			if (pausing) {
				paused = true;

				synchronized(Pause) {
					Pause.notifyAll();
				}
			}
		}
	}
	catch (NullPointerException e) {
		if (bscraper!= null) {
			System.out.println("ImagePanel.scrape(): " + e.getMessage());
			e.printStackTrace();
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/8/2002 2:20:39 PM)
 */
public void timeReceive() {
	receiveStartTime = -2;
}
}
