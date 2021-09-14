package oem.edge.ed.odc.dropbox.client;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
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
 * Creation date: (10/21/2002 9:26:50 AM)
 * @author: Mike Zarnick
 */
public class TimeCellRenderer extends JLabel implements TableCellRenderer {
	StringBuffer buffer = new StringBuffer();
/**
 * FileCellRenderer constructor comment.
 */
public TimeCellRenderer() {
	super();
	setOpaque(true);
	setHorizontalAlignment(RIGHT);
}
/**
 * FileCellRenderer constructor comment.
 * @param text java.lang.String
 */
public TimeCellRenderer(String text) {
	super(text);
}
/**
 * FileCellRenderer constructor comment.
 * @param text java.lang.String
 * @param horizontalAlignment int
 */
public TimeCellRenderer(String text, int horizontalAlignment) {
	super(text, horizontalAlignment);
}
/**
 * FileCellRenderer constructor comment.
 * @param text java.lang.String
 * @param icon javax.swing.Icon
 * @param horizontalAlignment int
 */
public TimeCellRenderer(String text, javax.swing.Icon icon, int horizontalAlignment) {
	super(text, icon, horizontalAlignment);
}
/**
 * FileCellRenderer constructor comment.
 * @param image javax.swing.Icon
 */
public TimeCellRenderer(javax.swing.Icon image) {
	super(image);
}
/**
 * FileCellRenderer constructor comment.
 * @param image javax.swing.Icon
 * @param horizontalAlignment int
 */
public TimeCellRenderer(javax.swing.Icon image, int horizontalAlignment) {
	super(image, horizontalAlignment);
}
/**
 * Insert the method's description here.
 * Creation date: (10/21/2002 9:29:43 AM)
 * @return java.awt.Component
 * @param l javax.swing.JList
 * @param c java.lang.Object
 * @param i int
 * @param d boolean
 * @param s boolean
 */
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	if (value != null) {
		long time = ((Long) value).longValue() / 1000;
		buffer.setLength(0);
		long hours = time / 3600;
		if (hours > 0) {
			if (hours < 10) buffer.append('0');
			buffer.append(hours);
			buffer.append(':');
			time -= hours * 3600;
		}
		long min = time / 60;
		if (min > 0) {
			if (min < 10) buffer.append('0');
			buffer.append(min);
			buffer.append(':');
			time -= min * 60;
		}
		else {
			buffer.append("00:");
		}
		if (time < 10) buffer.append('0');
		buffer.append(time);
		setText(buffer.toString());
	}
	else
		setText("");

	if (isSelected) {
		setBackground(table.getSelectionBackground());
		setForeground(table.getSelectionForeground());
	}
	else {
		setBackground(table.getBackground());
		setForeground(table.getForeground());
	}

	return this;
}
}
