package oem.edge.ed.odc.ftp.client;

import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
/**
 * Insert the type's description here.
 * Creation date: (11/6/2002 2:53:27 PM)
 * @author: Mike Zarnick
 */
public class FileStatusDirectionRenderer extends JLabel implements TableCellRenderer {
	static public ImageIcon UP = new ImageIcon(FileCellRenderer.class.getResource("/oem/edge/ed/odc/ftp/client/upload.gif"));
	static public ImageIcon DOWN = new ImageIcon(FileCellRenderer.class.getResource("/oem/edge/ed/odc/ftp/client/download.gif"));
/**
 * FileStatusDirectionRenderer constructor comment.
 */
public FileStatusDirectionRenderer() {
	super();

	setOpaque(true);
	setHorizontalAlignment(JLabel.CENTER);
	setVerticalAlignment(JLabel.CENTER);
}
/**
 * FileStatusDirectionRenderer constructor comment.
 * @param text java.lang.String
 */
public FileStatusDirectionRenderer(String text) {
	super(text);
}
/**
 * FileStatusDirectionRenderer constructor comment.
 * @param text java.lang.String
 * @param horizontalAlignment int
 */
public FileStatusDirectionRenderer(String text, int horizontalAlignment) {
	super(text, horizontalAlignment);
}
/**
 * FileStatusDirectionRenderer constructor comment.
 * @param text java.lang.String
 * @param icon javax.swing.Icon
 * @param horizontalAlignment int
 */
public FileStatusDirectionRenderer(String text, Icon icon, int horizontalAlignment) {
	super(text, icon, horizontalAlignment);
}
/**
 * FileStatusDirectionRenderer constructor comment.
 * @param image javax.swing.Icon
 */
public FileStatusDirectionRenderer(Icon image) {
	super(image);
}
/**
 * FileStatusDirectionRenderer constructor comment.
 * @param image javax.swing.Icon
 * @param horizontalAlignment int
 */
public FileStatusDirectionRenderer(Icon image, int horizontalAlignment) {
	super(image, horizontalAlignment);
}
/**
 *  This method is sent to the renderer by the drawing table to
 *  configure the renderer appropriately before drawing.  Return
 *  the Component used for drawing.
 *
 * @param	table		the JTable that is asking the renderer to draw.
 *				This parameter can be null.
 * @param	value		the value of the cell to be rendered.  It is
 *				up to the specific renderer to interpret
 *				and draw the value.  eg. if value is the
 *				String "true", it could be rendered as a
 *				string or it could be rendered as a check
 *				box that is checked.  null is a valid value.
 * @param	isSelected	true is the cell is to be renderer with
 *				selection highlighting
 * @param	row	        the row index of the cell being drawn.  When
 *				drawing the header the rowIndex is -1.
 * @param	column	        the column index of the cell being drawn
 */
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	if (value == Boolean.FALSE) {
		setIcon(DOWN);
		setToolTipText("File download from server");
	}
	else {
		setIcon(UP);
		setToolTipText("File upload to server");
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
