package oem.edge.ed.sd;

import java.awt.*;

/**
 * Insert the type's description here.
 * Creation date: (10/9/2001 9:39:05 AM)
 * @author: Mike Zarnick
 */
public class TableBorder extends Panel {
	public static int LEFT = 0;
	public static int CENTER = 1;
	public static int RIGHT = 2;
	protected String title = null;
	protected int justify = LEFT;
/**
 * TableBorder constructor comment.
 */
public TableBorder() {
	super();
}
/**
 * TableBorder constructor comment.
 * @param layout java.awt.LayoutManager
 */
public TableBorder(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2001 12:12:08 PM)
 * @return int
 */
public int getJustify() {
	return justify;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2001 12:11:40 PM)
 * @return java.lang.String
 */
public String getTitle() {
	return title;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2001 9:44:32 AM)
 * @param g java.awt.Graphics
 */
public void paint(Graphics g1) {
	
	Rectangle r = getBounds();
	Graphics g = g1.create(0,0,r.width,r.height);
	g.setColor(getForeground());

	// Handling a title?
	if (title != null && title.trim().length() > 0) {
		FontMetrics f = g.getFontMetrics();
		int h = f.getHeight();
		int d = f.getDescent();
		int w = f.stringWidth(title) + 4; // 2 empty pixels on each end.

		g.drawRect(0,(h/2),r.width - 1,r.height - (h/2) - 1);
		g.drawRect(1,(h/2) + 1,r.width - 3, r.height - (h/2) - 3);

		// title is wider than slot available?
		Graphics gs = null;

		if (w > r.width - 11) {
			// Fits within panel, just can't have full surrounding handles.
			if (w <= r.width - 5)
				gs = g.create(2,0,w,h);
			// Doesn't fit, use as much as reasonable.
			else
				gs = g.create(2,0,r.width - 5,h);
		}
		else if (justify == LEFT)
			gs = g.create(5,0,w,h);
		else if (justify == CENTER)
			gs = g.create(((r.width - 1)/2) - (w/2),0,w,h);
		else
			gs = g.create(r.width - w - 6,0,w,h);

		gs.setColor(getBackground());
		gs.fillRect(0,0,w,h);
		gs.setColor(getForeground());
		gs.drawString(title,2,h - d);
	}

	// No title set, draw a full rectangle.
	else {
		g.drawRect(0,0,r.width - 1,r.height - 1);
		g.drawRect(1,1,r.width - 3,r.height - 3);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2001 12:09:04 PM)
 * @param j int
 */
public void setJustify(int j) {
	if (j < LEFT || j > RIGHT)
		j = LEFT;

	justify = j;

	if (isShowing())
		repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2001 9:41:42 AM)
 * @param title java.lang.String
 */
public void setTitle(String title) {
	this.title = title;
	if (isVisible())
		repaint();
}
}
