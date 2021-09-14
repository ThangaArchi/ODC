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

import java.awt.*;
/**
 * Insert the type's description here.
 * Creation date: (2/15/2002 8:53:00 AM)
 * @author: Mike Zarnick
 */
public class PerformanceBar extends java.awt.Panel {
	int percent;
	boolean horizontal = true;
	Color red = Color.red;
	Color yellow = Color.yellow;
	Color green = Color.green;
/**
 * PerformanceBar constructor comment.
 */
public PerformanceBar() {
	super();
}
/**
 * PerformanceBar constructor comment.
 * @param layout java.awt.LayoutManager
 */
public PerformanceBar(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * Insert the method's description here.
 * Creation date: (2/15/2002 11:04:09 AM)
 * @return java.awt.Color
 */
public Color getGreen() {
	return green;
}
/**
 * Insert the method's description here.
 * Creation date: (2/15/2002 8:55:12 AM)
 * @param p int
 */
public boolean getHorizontal() {
	return horizontal;
}
/**
 * Insert the method's description here.
 * Creation date: (2/15/2002 8:54:52 AM)
 * @return int
 */
public int getPercent() {
	return percent;
}
/**
 * Insert the method's description here.
 * Creation date: (2/15/2002 11:04:09 AM)
 * @return java.awt.Color
 */
public Color getRed() {
	return red;
}
/**
 * Insert the method's description here.
 * Creation date: (2/15/2002 11:04:09 AM)
 * @return java.awt.Color
 */
public Color getYellow() {
	return yellow;
}
/**
 * Insert the method's description here.
 * Creation date: (2/15/2002 8:53:39 AM)
 * @param g java.awt.Graphics
 */
public void paint(java.awt.Graphics g) {
	Dimension d = getSize();

	if (horizontal) {
		int b = (d.width - 4) / 7;
		int x = d.width - 4 - (b * 7);

		if (x > 4)
			b++;

		int c = b * 5;
		int rc = (b / 6) * 5;
		int yc = (b / 5) * 5;

		// Paint the background
		g.setColor(getBackground());
		g.fillRect(0,0,d.width,d.height);

		// Determine how many columns are colored.
		int dc = c * percent / 100;

		// Render the red columns.
		int cc = 0;
		x = 2;

		g.setColor(getRed());
		for (int i = 0; i < rc && cc < dc; i++) {
			g.drawLine(x,2,x,d.height - 3);

			cc++;

			if (cc % 5 == 0)
				x += 3;
			else
				x++;
		}

		// Render the yellow columns.
		g.setColor(getYellow());
		for (int i = 0; i < yc && cc < dc; i++) {
			g.drawLine(x,2,x,d.height - 3);

			cc++;

			if (cc % 5 == 0)
				x += 3;
			else
				x++;
		}

		// Render the green columns.
		g.setColor(getGreen());
		while (cc < dc) {
			g.drawLine(x,2,x,d.height - 3);

			cc++;

			if (cc % 5 == 0)
				x += 3;
			else
				x++;
		}
	}
	else {
		int b = (d.height - 4) / 7;
		int y = d.height - 4 - (b * 7);

		if (y > 4)
			b++;

		int c = b * 5;
		int rc = (b / 6) * 5;
		int yc = (b / 5) * 5;

		// Paint the background
		g.setColor(getBackground());
		g.fillRect(0,0,d.width,d.height);

		// Determine how many columns are colored.
		int dc = c * percent / 100;

		// Render the red columns.
		int cc = 0;
		y = d.height - 3;

		g.setColor(getRed());
		for (int i = 0; i < rc && cc < dc; i++) {
			g.drawLine(2,y,d.width - 3,y);

			cc++;

			if (cc % 5 == 0)
				y -= 3;
			else
				y--;
		}

		// Render the yellow columns.
		g.setColor(getYellow());
		for (int i = 0; i < yc && cc < dc; i++) {
			g.drawLine(2,y,d.width - 3,y);

			cc++;

			if (cc % 5 == 0)
				y -= 3;
			else
				y--;
		}

		// Render the green columns.
		g.setColor(getGreen());
		while (cc < dc) {
			g.drawLine(2,y,d.width - 3,y);

			cc++;

			if (cc % 5 == 0)
				y -= 3;
			else
				y--;
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/15/2002 11:05:01 AM)
 * @param r java.awt.Color
 */
public void setGreen(Color g) {
	if (g != null)
		green = g;
}
/**
 * Insert the method's description here.
 * Creation date: (2/15/2002 8:55:12 AM)
 * @param p int
 */
public void setHorizontal(boolean h) {
	horizontal = h;

	repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (2/15/2002 8:55:12 AM)
 * @param p int
 */
public void setPercent(int p) {
	if (p < 0)
		percent = 0;
	else if (p > 100)
		percent = 100;
	else
		percent = p;

	repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (2/15/2002 11:05:01 AM)
 * @param r java.awt.Color
 */
public void setRed(Color r) {
	if (r != null)
		red = r;
}
/**
 * Insert the method's description here.
 * Creation date: (2/15/2002 11:05:01 AM)
 * @param r java.awt.Color
 */
public void setYellow(Color y) {
	if (y != null)
		yellow = y;
}
}
