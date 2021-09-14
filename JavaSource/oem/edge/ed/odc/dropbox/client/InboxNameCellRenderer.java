package oem.edge.ed.odc.dropbox.client;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTable;

import oem.edge.ed.odc.dropbox.common.PackageInfo;
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
 * Creation date: (05/24/2004)
 * @author: Aswathappa N
 */
public class InboxNameCellRenderer extends PkgNameCellRenderer {
	Icon newMail = null;
	Icon blank = null;
/**
 * InboxNameCellRenderer constructor comment.
 */
public InboxNameCellRenderer() {
	super();
	newMail = new ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/new_mail.gif"));
	blank = new ImageIcon(getClass().getResource("/oem/edge/ed/odc/dropbox/client/blank.gif"));
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
	PackageInfo p = (PackageInfo) value;
	if (p.getPackageCompleted()) {
		setIcon(blank);
	}
	else {
		setIcon(newMail);
	}

	return super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
}
}
