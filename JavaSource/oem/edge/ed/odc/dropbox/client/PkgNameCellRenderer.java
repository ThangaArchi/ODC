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

/**
 * Insert the type's description here.
 * Creation date: (05/24/2004)
 * @author: Aswathappa N
 */
public class PkgNameCellRenderer extends JLabel implements TableCellRenderer {
/**
 * InboxNameCellRenderer constructor comment.
 */
public PkgNameCellRenderer() {
	super();
	setOpaque(true);
	setHorizontalAlignment(LEFT);
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
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
{
	DisplayPackageInfo p = (DisplayPackageInfo) value;
	setText(p.getPackageName());

	if (p.getDescriptionAsToolTip() != null) {
		setToolTipText("<html>" + p.getDescriptionAsToolTip() + "</html>");
	}
	else {
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
