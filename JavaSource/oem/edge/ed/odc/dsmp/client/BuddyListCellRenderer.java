package oem.edge.ed.odc.dsmp.client;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
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
 * Creation date: (7/1/2004 10:54:55 AM)
 * @author: 
 */
public class BuddyListCellRenderer extends JLabel implements ListCellRenderer {
	private static Border noFocusBorder;
	private static Icon user;
	private static Icon group;
	private static Icon project;
/**
 * BuddyListCellRenderer constructor comment.
 */
public BuddyListCellRenderer() {
	super();
	noFocusBorder = new EmptyBorder(1,1,1,1);
	setOpaque(true);
	setHorizontalAlignment(LEFT);
	user = new ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/buddy.gif"));
	group = new ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/group.gif"));
	project = new ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/project.gif"));
}
/**
 * Insert the method's description here.
 * Creation date: (7/14/2004 12:40:54 PM)
 * @return java.awt.Component
 * @param list javax.swing.JList
 * @param value java.lang.Object
 * @param index int
 * @param isSelected boolean
 * @param cellHasFocus boolean
 */
public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	setComponentOrientation(list.getComponentOrientation());

	if (isSelected) {
		setBackground(list.getSelectionBackground());
		setForeground(list.getSelectionForeground());
	}
	else {
		setBackground(list.getBackground());
		setForeground(list.getForeground());
	}

	if (value instanceof Buddy) {
		Buddy buddy = (Buddy) value;
		if (buddy.type == Buddy.USER) {
			setIcon(user);
		}
		else if (buddy.type == Buddy.GROUP) {
			setIcon(group);
		}
		else {
			setIcon(project);
		}
		if (buddy.companyList != null && buddy.companyList.length() > 0) {
			setText(buddy.name + " (" + buddy.companyList + ")");
		}
		else {
			setText(buddy.name);
		}
	}
	else if (value instanceof Icon) {
		setIcon((Icon)value);
	}
	else {
		setText((value == null) ? "" : value.toString());
	}

	setEnabled(list.isEnabled());
	setFont(list.getFont());
	setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

	return this;
}
}
