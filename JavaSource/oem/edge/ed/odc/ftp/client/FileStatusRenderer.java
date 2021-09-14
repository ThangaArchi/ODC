package oem.edge.ed.odc.ftp.client;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * Insert the type's description here.
 * Creation date: (10/24/2002 1:15:47 PM)
 * @author: Mike Zarnick
 */
public class FileStatusRenderer implements TableCellRenderer {
	private JLabel label = new JLabel();
	private JProgressBar pbar = new JProgressBar();
/**
 * FileStatusRenderer constructor comment.
 */
public FileStatusRenderer() {
	super();
	pbar.setOpaque(true);
	pbar.setMinimum(0);
	pbar.setMaximum(100);
	pbar.setStringPainted(true);
	pbar.setForeground(Color.blue);
	label.setOpaque(true);
}
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	Component c = null;

	if (value instanceof Integer) {
		c = pbar;
		pbar.setValue(((Integer) value).intValue());

		if (isSelected)
			pbar.setBackground(table.getSelectionBackground());
		else
			pbar.setBackground(table.getBackground());
	}
	else {
		c = label;
		label.setText(value.toString());

		if (isSelected) {
			label.setBackground(table.getSelectionBackground());
			label.setForeground(table.getSelectionForeground());
		}
		else {
			label.setBackground(table.getBackground());
			label.setForeground(table.getForeground());
		}
	}

	return c;
}
}
