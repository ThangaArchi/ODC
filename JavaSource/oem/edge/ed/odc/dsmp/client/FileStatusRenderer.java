package oem.edge.ed.odc.dsmp.client;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
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
