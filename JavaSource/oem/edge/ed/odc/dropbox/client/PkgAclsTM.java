package oem.edge.ed.odc.dropbox.client;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.table.TableCellRenderer;

import oem.edge.ed.odc.dropbox.common.AclInfo;
import oem.edge.ed.odc.dropbox.common.DropboxGenerator;
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

public class PkgAclsTM extends javax.swing.table.AbstractTableModel {
	private int[] columnWidths = { 125, 100, 85 };
	private String[] columnNames = { "Recipient", "Company", "Status" };
	private Class[] columnClasses = { String.class, String.class, String.class };
	private TableCellRenderer[] columnRenderers = { null, null, null };
	private Vector rows = new Vector();
/**
 * FileAclsTM constructor comment.
 */
public PkgAclsTM() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2003 5:02:39 PM)
 * @param v java.util.Vector
 */
public void addAcls(Vector v) {
	rows.removeAllElements();
	Enumeration e = v.elements();
	while (e.hasMoreElements()) {
		AclInfo a = (AclInfo) e.nextElement();
		if (a.getAclStatus() != DropboxGenerator.STATUS_PROJECT)
			rows.addElement(a);
	}
	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2003 2:47:45 PM)
 */
public void clear() {
	rows.removeAllElements();
	fireTableDataChanged();
}
/**
 * getColumnClass method comment.
 */
public Class getColumnClass(int c) {
	return columnClasses[c];
}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return columnNames.length;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2003 3:00:41 PM)
 * @return java.lang.String
 * @param c int
 */
public String getColumnName(int c) {
	return columnNames[c];
}
/**
 * getColumnWidth method comment.
 */
public TableCellRenderer getColumnRenderer(int c) {
	return columnRenderers[c];
}
/**
 * getColumnWidth method comment.
 */
public int getColumnWidth(int c) {
	return columnWidths[c];
}
/**
 * getRowCount method comment.
 */
public int getRowCount() {
	return rows.size();
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int r, int c) {
	AclInfo a = (AclInfo) rows.elementAt(r);

	if (a != null) {
		switch (c) {
			case 0: {
				if (a.getAclProjectName() == null || a.getAclProjectName().equals(""))
					return a.getAclName();
				else
					return a.getAclName() + " (" + a.getAclProjectName() + ")";
			}
			case 1: {
				return a.getAclCompany();
			}
			case 2: {
				if (a.getAclStatus() == DropboxGenerator.STATUS_NONE)
					return "None";
				else if (a.getAclStatus() == DropboxGenerator.STATUS_PARTIAL)
					return "Partial";
				else if (a.getAclStatus() == DropboxGenerator.STATUS_FAIL)
					return "Failed";
				else
					return "Complete";
			}
		}
	}

	return null;
}
}
