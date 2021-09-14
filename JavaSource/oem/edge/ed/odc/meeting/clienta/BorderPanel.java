package oem.edge.ed.odc.meeting.clienta;

import java.awt.*;

/**
 * Insert the type's description here.
 * Creation date: (10/9/2001 9:39:05 AM)
 * @author: Mike Zarnick
 */
public class BorderPanel extends Panel {
	public static int LEFT = 0;
	public static int CENTER = 1;
	public static int RIGHT = 2;
	protected String title = null;
	protected int justify = LEFT;
/**
 * TableBorder constructor comment.
 */
public BorderPanel() {
	super();
}
/**
 * TableBorder constructor comment.
 * @param layout java.awt.LayoutManager
 */
public BorderPanel(java.awt.LayoutManager layout) {
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
public void paint(Graphics g) {
	update(g);
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
	if (title != null && title.trim().length() == 0)
		title = null;

	this.title = title;

	if (isVisible())
		repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2001 9:44:32 AM)
 * @param g java.awt.Graphics
 */
public void update(Graphics g1) {
	Rectangle r = getBounds();
	// Clip rectangle does not appear to always be set properly... this fixes that.
	Graphics g = g1.create(0,0,r.width,r.height);
	g.clearRect(0,0,r.width,r.height);
	g.setColor(getForeground());

	// Big enough to at least draw a border?
	if (r.width > 5 && r.height > 5) {
		// Get font stuff to check title.
		FontMetrics f = g.getFontMetrics();
		int h = f.getHeight();
		int y = h/2;

		// Can handle a title if necessary?
		if (title != null && r.width > 12 && r.height > h+2) {
			int d = f.getDescent();
			int w = f.stringWidth(title) + 4; // 2 empty pixels on each end.

			g.drawRect(0,y,r.width - 1,r.height - y - 1);
			g.drawRect(1,y+1,r.width - 3, r.height - y - 3);
			/*Rectangle cr = g.getClipBounds();

			// Render left edge of Rectangle.
			if (cr.x <= 1 && cr.x + cr.width >= 1)
				g.drawLine(1,y+1,1,cr.y+cr.height-3);
			if (cr.x == 0)
				g.drawLine(0,y,0,cr.y+cr.height-1);

			// Render right edge of Rectangle.
			if (cr.x <= r.width-2 && cr.x + cr.width >= r.width - 2)
				g.drawLine(r.width-2,y+1,r.width-2,cr.y+cr.height-3);
			if (cr.x <= r.width-1 && cr.x + cr.width >= r.width - 1)
				g.drawLine(r.width-1,y,r.width-1,cr.y+cr.height-1);

			// Render top edge of Rectangle.
			if (cr.y <= y && cr.y + cr.height >= y)
				g.drawLine(cr.x,y,cr.x+cr.width,y);
			if (cr.y <= y+1 && cr.y + cr.height >= y+1)
				g.drawLine(cr.x,y+1,cr.x+cr.width,y+1);

			// Render bottom edge of Rectangle.
			if (cr.y <= r.height-2 && cr.y + cr.height >= r.height-2)
				g.drawLine(cr.x,r.height-2,cr.x+cr.width,r.height-2);
			if (cr.y <= r.height-1 && cr.y + cr.height >= r.height-1)
				g.drawLine(cr.x,r.height-1,cr.x+cr.width,r.height-1);*/

			Graphics gs = null;

			// title is wider than slot available?
			if (w > r.width - 12)
				gs = g.create(4,0,r.width - 8,h);
			else if (justify == LEFT)
				gs = g.create(4,0,w,h);
			else if (justify == CENTER)
				gs = g.create(r.width/2 - w/2,0,w,h);
			else
				gs = g.create(r.width - w - 4,0,w,h);

			gs.setColor(getBackground());
			gs.fillRect(0,0,w,h);
			gs.setColor(getForeground());
			gs.drawString(title,2,h - d);
		}

		// No title or can not be handled, draw a full rectangle.
		else {
			g.drawRect(0,0,r.width - 1,r.height - 1);
			g.drawRect(1,1,r.width - 3,r.height - 3);
		}
	}
}
}
