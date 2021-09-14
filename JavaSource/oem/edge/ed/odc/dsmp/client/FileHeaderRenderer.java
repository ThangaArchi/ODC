package oem.edge.ed.odc.dsmp.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
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

/**
 * Insert the type's description here.
 * Creation date: (10/24/2002 10:45:44 AM)
 * @author: Mike Zarnick
 */
public class FileHeaderRenderer extends DefaultTableCellRenderer {
	private boolean sortup;
	private boolean sortdown;
/**
 * FileHeaderRenderer constructor comment.
 */
public FileHeaderRenderer() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2002 10:51:03 AM)
 * @return java.awt.Component
 * @param table javax.swing.JTable
 * @param value java.lang.Object
 * @param isSelected boolean
 * @param hasFocus boolean
 * @param row int
 * @param column int
 */
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	sortup = false;
	sortdown = false;

	if (table != null) {
		JTableHeader header = table.getTableHeader();
		if (header != null) {
			setForeground(header.getForeground());
			setBackground(header.getBackground());
			setFont(header.getFont());
		}

		if (table.getModel() instanceof TableSorter) {
			TableSorter ts = (TableSorter) table.getModel();
			if (ts.sortingColumn == column) {
				sortup = ts.ascending;
				sortdown = ! ts.ascending;
			}
		}
	}

	setText((value == null) ? "" : value.toString());
	setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	return this;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2002 10:55:26 AM)
 * @param g java.awt.Graphics
 */
public void paintComponent(Graphics g) {
	super.paintComponent(g);

	// Now paint the up and down arrow if this is being sorted.
	if (sortup || sortdown) {
		int w = getWidth();
		int h = getHeight();

		g.setColor(Color.black);

		int x[] = new int[3];
		int y[] = new int[3];

		x[0] = w - 14;
		x[1] = x[0] + 10;
		x[2] = x[0] + 5;

		if (sortup) {
			y[0] = h / 2 + 2;
			y[1] = y[0];
			y[2] = y[0] - 4;
		}
		else {
			y[0] = h / 2 - 2;
			y[1] = y[0];
			y[2] = y[0] + 4;
		}

		g.fillPolygon(x,y,3);
	}
}
}
