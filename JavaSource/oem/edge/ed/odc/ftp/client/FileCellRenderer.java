package oem.edge.ed.odc.ftp.client;

import java.io.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.*;
import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (10/21/2002 9:26:50 AM)
 * @author: Mike Zarnick
 */
public class FileCellRenderer extends JLabel implements TableCellRenderer {
	static public ImageIcon FILE = new ImageIcon(FileCellRenderer.class.getResource("/oem/edge/ed/odc/ftp/client/document.gif"));
	static public ImageIcon DIRECTORY = new ImageIcon(FileCellRenderer.class.getResource("/oem/edge/ed/odc/ftp/client/folder.gif"));
	static private SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
	static private NumberFormat numberFormatter = NumberFormat.getInstance();
/**
 * FileCellRenderer constructor comment.
 */
public FileCellRenderer() {
	super();
	setOpaque(true);
}
/**
 * FileCellRenderer constructor comment.
 * @param text java.lang.String
 */
public FileCellRenderer(String text) {
	super(text);
}
/**
 * FileCellRenderer constructor comment.
 * @param text java.lang.String
 * @param horizontalAlignment int
 */
public FileCellRenderer(String text, int horizontalAlignment) {
	super(text, horizontalAlignment);
}
/**
 * FileCellRenderer constructor comment.
 * @param text java.lang.String
 * @param icon javax.swing.Icon
 * @param horizontalAlignment int
 */
public FileCellRenderer(String text, javax.swing.Icon icon, int horizontalAlignment) {
	super(text, icon, horizontalAlignment);
}
/**
 * FileCellRenderer constructor comment.
 * @param image javax.swing.Icon
 */
public FileCellRenderer(javax.swing.Icon image) {
	super(image);
}
/**
 * FileCellRenderer constructor comment.
 * @param image javax.swing.Icon
 * @param horizontalAlignment int
 */
public FileCellRenderer(javax.swing.Icon image, int horizontalAlignment) {
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
	switch (column) {
		case 0:
			Boolean b = (Boolean) value;
			if (b.booleanValue())
				setIcon(DIRECTORY);
			else
				setIcon(FILE);
			setText(null);
			setHorizontalAlignment(CENTER);
			setToolTipText(null);
			break;
		case 1:
			setIcon(null);
			setText((String) value);
			setHorizontalAlignment(LEFT);
			setToolTipText(null);
			break;
		case 2:
			setIcon(null);
			Long l = (Long) value;
			if (l.longValue() != -1) {
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
			}
			else {
				setText("");
				setToolTipText(null);
			}
			setHorizontalAlignment(RIGHT);
			break;
		case 3:
			setIcon(null);
			Date d = new Date(((Long) value).longValue());
			setText(dateFormatter.format(d));
			setToolTipText(null);
			break;
		default:
			setIcon(null);
			setText(value.toString());
			setHorizontalAlignment(LEFT);
			setToolTipText(null);
	}

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
