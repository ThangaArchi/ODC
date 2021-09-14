package oem.edge.ed.odc.dropbox.client;

import java.awt.Component;
import java.text.NumberFormat;

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
public class SizeCellRenderer extends JLabel implements TableCellRenderer {
	static private NumberFormat numberFormatter = NumberFormat.getInstance();
/**
 * FileCellRenderer constructor comment.
 */
public SizeCellRenderer() {
	super();
	setOpaque(true);
	setHorizontalAlignment(RIGHT);
}
/**
 * FileCellRenderer constructor comment.
 * @param text java.lang.String
 */
public SizeCellRenderer(String text) {
	super(text);
}
/**
 * FileCellRenderer constructor comment.
 * @param text java.lang.String
 * @param horizontalAlignment int
 */
public SizeCellRenderer(String text, int horizontalAlignment) {
	super(text, horizontalAlignment);
}
/**
 * FileCellRenderer constructor comment.
 * @param text java.lang.String
 * @param icon javax.swing.Icon
 * @param horizontalAlignment int
 */
public SizeCellRenderer(String text, javax.swing.Icon icon, int horizontalAlignment) {
	super(text, icon, horizontalAlignment);
}
/**
 * FileCellRenderer constructor comment.
 * @param image javax.swing.Icon
 */
public SizeCellRenderer(javax.swing.Icon image) {
	super(image);
}
/**
 * FileCellRenderer constructor comment.
 * @param image javax.swing.Icon
 * @param horizontalAlignment int
 */
public SizeCellRenderer(javax.swing.Icon image, int horizontalAlignment) {
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
	Long l = (Long) value;

	// Break down value to x.yy KB, MB or GB.
	long size = l.longValue();
	long divisor = 1024;
	String suffix = " KB";
	long whole = size / divisor;
	int fraction = 0;

	if (whole > 999) {
		divisor = 1048576;
		suffix = " MB";
		whole = size / divisor;
		if (whole > 999) {
			divisor = 1073741824;
			suffix = " GB";
			whole = size / divisor;
		}
	}

	fraction = (int) (((size - (whole * divisor)) * 100) / divisor);

	if (fraction == 0) {
		setText(whole + suffix);
	}
	else if (fraction < 10) {
		setText(whole + ".0" + fraction + suffix);
	}
	else {
		setText(whole + "." + fraction + suffix);
	}

	setToolTipText(numberFormatter.format(size) + " bytes");

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
