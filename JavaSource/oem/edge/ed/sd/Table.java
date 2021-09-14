package oem.edge.ed.sd;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

/**
 * Insert the type's description here.
 * Creation date: (10/4/2001 9:07:27 AM)
 * @author: Mike Zarnick
 */
public class Table extends Panel implements ItemListener {
	private Vector selections = new Vector();
	private oem.edge.ed.sd.SharedTableModel tm = null;
/**
 * Table constructor comment.
 */
public Table() {
	super();
	initialize();
}
/**
 * Table constructor comment.
 * @param layout java.awt.LayoutManager
 */
public Table(LayoutManager layout) {
	super(layout);
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("Table");
		setLayout(new java.awt.GridBagLayout());
		setSize(426, 240);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 2:31:35 PM)
 * @param e java.awt.event.ItemEvent
 */
public void itemStateChanged(ItemEvent e) {
	int i = selections.indexOf(e.getSource());
	Boolean b = new Boolean(e.getStateChange() == e.SELECTED);
	tm.setValueAt(b,i,0);
}
/**
 * Insert the method's description here.
 * Creation date: (10/4/2001 2:59:27 PM)
 * @param g java.awt.Graphics
 */
public void paint(Graphics g1) {
	// Paint everything
	Rectangle r = getBounds();
	Graphics g = g1.create(0,0,r.width,r.height);
	//g.setColor(getBackground());
	g.clearRect(r.x,r.y,r.width,r.height);

	super.paint(g);

	// Now paint the table grid.
	GridBagLayout l = (GridBagLayout) getLayout();
	int[][] d = l.getLayoutDimensions();

	g.setColor(getForeground());

	int maxx = 0;
	int maxy = 0;
	// Do the row heights
	for (int i = 0; i < d[1].length; i++)
		maxy += d[1][i];

	// Do the column widths
	for (int i = 0; i < d[0].length; i++)
		maxx += d[0][i];

	int x = 0;
	int y = 0;
	// Do the horizontal draws
	for (int i = 0; i < d[1].length; i++) {
		y += d[1][i];
		g.drawLine(0,y,maxx,y);
	}

	// Do the vertical draws
	for (int i = 0; i < d[0].length; i++) {
		x += d[0][i];
		g.drawLine(x,0,x,maxy);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2001 9:37:09 AM)
 * @param b boolean
 */
public void selectAll(boolean b) {
	Boolean B = new Boolean(b);
	for (int i = 0; i < selections.size(); i++) {
		Checkbox cb = (Checkbox) selections.elementAt(i);
		if (cb.isEnabled()) {
			cb.setState(b);
			tm.setValueAt(B,i,0);
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/4/2001 9:37:17 AM)
 * @param data oem.edge.ed.odc.applet.TableData
 */
public void setTableData(oem.edge.ed.sd.SharedTableModel data) {
	System.out.println("INSIDE SET TABLE DATA 1");
	if (tm != null) {
		removeAll();
		selections.removeAllElements();
	}
	
System.out.println("INSIDE SET TABLE DATA 2");
	tm = data;

	if (tm != null) {
		Label l = null;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.insets = new Insets(2,4,2,4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;

		int rc = tm.getRowCount();
		int cc = tm.getColumnCount();
		int i,j;

		for (j = 1; j < cc; j++) {
			l = new Label((String) tm.getColumnName(j),Label.CENTER);
			l.setBackground(SystemColor.control);
			l.setForeground(SystemColor.controlText);
			if (j != cc - 1) {
				add(l,gbc);
				gbc.gridx++;
			}
		}
System.out.println("INSIDE SET TABLE DATA 3");
		if (rc > 0) {
			add(l,gbc);

			gbc.anchor = GridBagConstraints.NORTHWEST;

			for (i = 0; i < rc; i++) {
				gbc.gridy++;

				Checkbox cb = new Checkbox((String) tm.getValueAt(i,1));
				Boolean B = (Boolean)tm.getValueAt(i,0);
				if(B.booleanValue())
					cb.setState(true);
				cb.addItemListener(this);
				selections.addElement(cb);
				gbc.gridx = 0;
				add(cb,gbc);

				for (j = 2; j < cc; j++) {
					String value = (String) tm.getValueAt(i,j);
					l = new Label(value);
					if (j == cc - 1)
						l.setAlignment(l.RIGHT);
					else if (j == 3 && value.equalsIgnoreCase("expired")){
						cb.setEnabled(false);
						cb.setState(false);
					}
					gbc.gridx++;

					if (i != rc - 1 || j != cc - 1)
						add(l,gbc);
				}
			}

			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			add(l,gbc);
		}
		else if (cc > 0) {
			gbc.weighty = 1.0;
			add(l,gbc);
		}
System.out.println("INSIDE SET TABLE DATA 4");
		validate();
		System.out.println("INSIDE SET TABLE DATA 5");
	}
}

/**
 * Insert the method's description here.
 * Creation date: (10/10/2001 9:37:09 AM)
 * @param b boolean
 */
public void select(int row,boolean b) {
	if (row < tm.getRowCount()) {
		Boolean B = new Boolean(b);
		Checkbox cb = (Checkbox) selections.elementAt(row);
		cb.setState(b);
		tm.setValueAt(B,row,0);
	}
}
}
