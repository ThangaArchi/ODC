package oem.edge.ed.odc.dropbox.client.soa;

/**
 * Insert the type's description here.
 * Creation date: (1/15/2003 2:56:51 PM)
 * @author: Mike Zarnick
 */

import java.awt.Component;
import java.rmi.RemoteException;
import java.util.*;

import oem.edge.ed.odc.dropbox.client.DateCellRenderer;
import oem.edge.ed.odc.dropbox.client.DropboxTableModel;
import oem.edge.ed.odc.dropbox.client.SizeCellRenderer;
import oem.edge.ed.odc.dropbox.common.PackageInfo;
import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dsmp.client.ErrorRunner;
import oem.edge.ed.odc.dsmp.common.DboxException;

import javax.swing.SwingUtilities;
import javax.swing.table.*;
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

public class InboxPkgTM extends AbstractTableModel implements DropboxTableModel {
	private int[] columnWidths = { 20, 150, 100, 100, 85, 145, 145 };
	private String[] columnNames = { " ","Package", "From", "Company", "Size", "Delivery", "Expiration" };
	private String[] logicalColumnNames = { "mark","package", "from", "company", "size", "deliver", "expiration" };
	private Class[] columnClasses = { Boolean.class, DisplayPackageInfo.class, String.class, String.class, Long.class, Long.class, Long.class };
	private TableCellRenderer[] columnRenderers = {null,new InboxNameCellRenderer(), null, null, new SizeCellRenderer(), new DateCellRenderer(), new DateCellRenderer() };
	private Vector rows = new Vector();
	private DropboxAccess dboxAccess = null;
	private Component parent = null;
/**
 * Package marking handler.
 */
private class MarkHandler implements Runnable {
	public int row;
	public long pkgId;
	public boolean marked;
	public void failed() {
		DisplayPackageInfo pkg = (DisplayPackageInfo) rows.elementAt(row);
		pkg.setPackageMarked(! pkg.getPackageMarked());
		fireTableCellUpdated(row,0);
	}
	public void run() {
		try {
			dboxAccess.markPackage(pkgId,marked);
		} catch(DboxException e) {
			SwingUtilities.invokeLater(new MethodRunner(this,"failed"));
			SwingUtilities.invokeLater(new ErrorRunner(parent,e.getMessage(),"Mark Package Failed"));
		} catch(RemoteException re) {
			SwingUtilities.invokeLater(new MethodRunner(this,"failed"));
			SwingUtilities.invokeLater(new ErrorRunner(parent,re.getMessage(),"Mark Package Failed"));
		}
	}
}
/**
 * InboxPkgTM constructor comment.
 */
public InboxPkgTM() {
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
	DisplayPackageInfo p = (DisplayPackageInfo) rows.elementAt(r);
	return p;
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
 * Insert the method's description here.
 * Creation date: (2/3/2003 9:39:58 AM)
 * @return String
 * @param pID long
 */
public String getPackageName(long pID) {
	int i = getPackageRow(pID);

	if (i != -1)
		return getPackageName(i);

	return null;
}
/**
 * Insert the method's description here.
 * @return String
 * @param r int
 */
public String getPackageOwner(int r) {
	DisplayPackageInfo p = (DisplayPackageInfo) rows.elementAt(r);
	return p.getPackageOwner();
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/2003 3:59:21 PM)
 * @return int
 * @param pID long
 */
public int getPackageRow(long pID) {
	for (int i = 0; i < rows.size(); i++) {
		DisplayPackageInfo p = (DisplayPackageInfo) rows.elementAt(i);
		if (p.getPackageId() == pID) {
			return i;
		}
	}

	return -1;
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
				return (p.getPackageMarked() ? Boolean.TRUE : Boolean.FALSE);
			}
			case 1: {
				return p;
			}
			case 2: {
				return p.getPackageOwner();
			}
			case 3: {
				return p.getPackageCompany();
			}
			case 4: {
				return p.getSize();
			}
			case 5: {
				return p.getCommitDate();
			}
			case 6: {
				return p.getExpirationDate();
			}
		}
	}

	return null;
}
/*** Aswath 4/June/2004 ***********/
/**
 * isCellEditable method comment.
 */
public boolean isCellEditable(int row, int column) {
	if (column < 1) {
		return true;
	}
	else {
		return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/5/2004 2:34:02 PM)
 * @param newDispatcher oem.edge.ed.odc.dropbox.client.DBDispatcher
 */
public void setDropboxAccess(DropboxAccess newDboxAccess) {
	if (dboxAccess != null) {
		// Nothing to do.
	}

	dboxAccess = newDboxAccess;

	if (dboxAccess != null) {
		// Nothing to do.
	}
}
public void setParent(Component newParent) {
	if (parent != null) {
		// Nothing to do.
	}

	parent = newParent;

	if (parent != null) {
		// Nothing to do.
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6/16/2004 10:09:25 AM)
 * @param value java.lang.Object
 * @param r int
 * @param c int
 */
public void setValueAt(Object value, int r, int c) {
	if (c != 0)
		return;

	Boolean v = (Boolean) value;

	DisplayPackageInfo pkg = (DisplayPackageInfo) rows.elementAt(r);

	MarkHandler h = new MarkHandler();
	h.row = r;
	h.pkgId = pkg.getPackageId();
	h.marked = v.booleanValue();
	WorkerThread t = new WorkerThread(h);
	t.start();

	pkg.setPackageMarked(v.booleanValue());
	fireTableCellUpdated(r,c);
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
