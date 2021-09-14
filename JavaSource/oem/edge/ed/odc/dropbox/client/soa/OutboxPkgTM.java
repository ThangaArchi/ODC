package oem.edge.ed.odc.dropbox.client.soa;

/**
 * Insert the type's description here.
 * Creation date: (1/15/2003 2:56:51 PM)
 * @author: Mike Zarnick
 */

import java.util.*;
import javax.swing.table.*;

import oem.edge.ed.odc.dropbox.client.DateCellRenderer;
import oem.edge.ed.odc.dropbox.client.DropboxTableModel;
import oem.edge.ed.odc.dropbox.client.SizeCellRenderer;
import oem.edge.ed.odc.dropbox.common.PackageInfo;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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

public class OutboxPkgTM extends AbstractTableModel implements DropboxTableModel {
	private int[] columnWidths = { 150, 85, 145, 145 };
	private String[] columnNames = { "Package", "Size", "Delivery", "Expiration" };
	private String[] logicalColumnNames = { "package", "size", "deliver", "expiration" };
	private Class[] columnClasses = { DisplayPackageInfo.class, Long.class, Long.class, Long.class };
	private TableCellRenderer[] columnRenderers = { new PkgNameCellRenderer(), new SizeCellRenderer(), new DateCellRenderer(), new DateCellRenderer() };
	private Vector rows = new Vector();
/**
 * InboxPkgTM constructor comment.
 */
public OutboxPkgTM() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 9:00:42 AM)
 * @param p oem.edge.ed.odc.dropbox.common.PackageInfo
 */
public void addPackage(PackageInfo p) {
	int i = rows.size();
	rows.addElement(new DisplayPackageInfo(p));
	fireTableRowsInserted(i,i);
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 9:00:42 AM)
 * @param p java.util.Vector
 */
public void addPackages(Vector p) {
	if (rows.size() > 0) {
		rows.removeAllElements();
		fireTableDataChanged();
	}

	for (int i = 0; i < p.size(); i++) {
		addPackage((PackageInfo) p.elementAt(i));
	}
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
 * Insert the method's description here.
 * Creation date: (1/15/2003 3:00:41 PM)
 * @return java.lang.String
 * @param c int
 */
public String getLogicalColumnName(int c) {
	return logicalColumnNames[c];
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2003 9:39:58 AM)
 * @return long
 * @param r int
 */
public long getPackageID(int r) {
	DisplayPackageInfo p = (DisplayPackageInfo) rows.elementAt(r);
	return p.getPackageId();
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2003 9:39:58 AM)
 * @return long
 * @param r int
 */
public PackageInfo getPackageInfo(int r) {
	return (PackageInfo) rows.elementAt(r);
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2003 9:39:58 AM)
 * @return String
 * @param r int
 */
public String getPackageName(int r) {
	DisplayPackageInfo p = (DisplayPackageInfo) rows.elementAt(r);
	return p.getPackageName();
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
	DisplayPackageInfo p = (DisplayPackageInfo) rows.elementAt(r);

	if (p != null) {
		switch (c) {
			case 0: {
				return p;
			}
			case 1: {
				return p.getSize();
			}
			case 2: {
				return p.getCommitDate();
			}
			case 3: {
				return p.getExpirationDate();
			}
		}
	}

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (2/4/2003 10:52:45 AM)
 * @param p oem.edge.ed.odc.dropbox.common.PackageInfo
 */
public void updatePackage(PackageInfo np) {
	for (int i = 0; i < rows.size(); i++) {
		DisplayPackageInfo p = (DisplayPackageInfo) rows.elementAt(i);
		if (p.getPackageId() == np.getPackageId()) {
			p.copyPackageInfo(np);
			fireTableRowsUpdated(i,i);
			return;
		}
	}
}
}
