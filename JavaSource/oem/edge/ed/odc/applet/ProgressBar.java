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
 * Creation date: (8/13/2001 12:02:41 PM)
 * @author: Jeetendra Rao
 */
public class ProgressBar extends java.awt.Panel implements Runnable{
	private int completion;
	static private Color fg = new Color(0,66,99);
/**
 * ProgressBar constructor comment.
 */
public ProgressBar() {
	super();
	completion =20;
	
	//Color c = new Color(0,0,255);
	//setBackground(c);
	
}
/**
 * ProgressBar constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ProgressBar(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * Insert the method's description here.
 * Creation date: (8/13/2001 1:27:18 PM)
 * @param g java.awt.Graphics
 */
public void paint(java.awt.Graphics g) {
	java.awt.Rectangle r = getBounds();
	g.setColor(Color.black);
	g.drawRect(0,0,r.width-1,r.height-1);
	g.setColor(fg);
	g.fillRect(2,2,(r.width * completion / 100) - 4,r.height - 4);
}
/**
 * Insert the method's description here.
 * Creation date: (8/13/2001 3:42:47 PM)
 */
public void run() {
	System.out.println("Inside run in Progress Bar");
	
	}
/**
 * Insert the method's description here.
 * Creation date: (8/13/2001 3:36:27 PM)
 * @param comp int
 */
public void setCompletion(int comp) {
	if (comp > completion)
		for (int i = completion; i < comp; i++)
			repaint();

	completion = comp;

	repaint();
}
}
