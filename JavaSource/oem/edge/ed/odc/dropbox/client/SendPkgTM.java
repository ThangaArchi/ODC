package oem.edge.ed.odc.dropbox.client;

/**
 * Insert the type's description here.
 * Creation date: (1/15/2003 2:56:51 PM)
 * @author: Mike Zarnick
 */

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import oem.edge.ed.odc.dropbox.common.AclInfo;
import oem.edge.ed.odc.dropbox.common.DropboxGenerator;
import oem.edge.ed.odc.dropbox.common.PackageInfo;
import oem.edge.ed.odc.dsmp.client.FileStatusEvent;
import oem.edge.ed.odc.dsmp.client.FileStatusListener;
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

public class SendPkgTM extends AbstractTableModel implements DropboxTableModel,FileStatusListener {
	private int[] columnWidths = { 150, 100, 85, 145, 145 };
	private String[] columnNames = { "Package", "Status", "Size", "Creation", "Expiration" };
	private String[] logicalColumnNames = { "package", "status", "size", "creation", "expiration" };
	private Class[] columnClasses = { DisplayPackageInfo.class, String.class, Long.class, Long.class, Long.class };
	private TableCellRenderer[] columnRenderers = { new PkgNameCellRenderer(), null, new SizeCellRenderer(), new DateCellRenderer(), new DateCellRenderer() };
	private Vector rows = new Vector();
	private Vector ids = new Vector();
	private Hashtable filesActive = new Hashtable();
/**
 * InboxPkgTM constructor comment.
 */
public SendPkgTM() {
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
	ids.addElement(new Long(p.getPackageId()));
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
		ids.removeAllElements();
		fireTableDataChanged();
	}

	for (int i = 0; i < p.size(); i++) {
		addPackage((PackageInfo) p.elementAt(i));
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/4/2003 10:17:14 AM)
 * @param pID long
 * @param a oem.edge.ed.odc.dropbox.common.AclInfo
 */
public void addRecipient(long pID, AclInfo a) {
	for (int i = 0; i < rows.size(); i++) {
		DisplayPackageInfo p = (DisplayPackageInfo) rows.elementAt(i);
		if (p.getPackageId() == pID) {
			p.addRecipient(a);
			return;
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/4/2003 10:17:14 AM)
 * @param pID long
 * @param a oem.edge.ed.odc.dropbox.common.AclInfo
 */
public void addRecipients(long pID, Vector acls) {
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2003 2:47:45 PM)
 */
public void clear() {
	rows.removeAllElements();
	ids.removeAllElements();
	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2003 5:14:11 PM)
 * @param e oem.edge.ed.odc.dropbox.client.FileStatusEvent
 */
public synchronized void fileStatusAction(FileStatusEvent e) {
	// Only want upload events.
	if (! e.isUpload)
		return;

	// If event is for a file upload which has begun.
	if (e.isFileBegun()) {
		// Keep track of files in upload.
		Long pkgID = new Long(e.packageID);

		Integer cnt = (Integer) filesActive.get(pkgID);
		boolean isNew = false;

		if (cnt != null) {
			cnt = new Integer(cnt.intValue()+1);
		}
		else {
			cnt = new Integer(1);
			isNew = true;
		}

		filesActive.put(pkgID,cnt);

		if (isNew) {
			int r = getPackageRow(e.packageID);
			if (r != -1) {
				fireTableCellUpdated(r,1);
			}
		}
	}

	// If event is for an ended file upload.
	else if (e.isFileEnded()) {
		// Keep track of files in upload.
		Long pkgID = new Long(e.packageID);

		Integer cnt = (Integer) filesActive.get(pkgID);

		if (cnt != null) {
			if (cnt.intValue() > 1) {
				cnt = new Integer(cnt.intValue()-1);
				filesActive.put(pkgID,cnt);
			}
			else {
				filesActive.remove(pkgID);
				int r = getPackageRow(e.packageID);
				if (r != -1) {
					fireTableCellUpdated(r,1);
				}
			}
		}
	}
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
	Long id = (Long) ids.elementAt(r);
	return id.longValue();
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
 * Insert the method's description here.
 * Creation date: (2/3/2003 9:39:58 AM)
 * @return int
 * @param id long
 */
public int getPackageRow(long id) {
	for (int i = 0; i < rows.size(); i++) {
		DisplayPackageInfo p = (DisplayPackageInfo) rows.elementAt(i);
		if (p.getPackageId() == id) {
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
				return p;
			}
			case 1: {
				Long pkgID = (Long) ids.elementAt(r);
				Integer cnt = (Integer) filesActive.get(pkgID);

				if (cnt != null) {
					if (cnt.intValue() == 1)
						return "Adding file";
					else
						return "Adding files";
				}

				if (p.getPackageStatus() == DropboxGenerator.STATUS_NONE)
					return "Empty";
				else if (p.getPackageStatus() == DropboxGenerator.STATUS_PARTIAL)
					return "Ready to send";
				else
					return "File errors";
			}
			case 2: {
				return p.getSize();
			}
			case 3: {
				return p.getCreationDate();
			}
			case 4: {
				return p.getExpirationDate();
			}
		}
	}

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (8/14/2003 10:44:56 AM)
 * @return boolean
 * @param row int
 */
public boolean isBusy(int r) {
	Long pkgID = (Long) ids.elementAt(r);
	Integer count = (Integer) filesActive.get(pkgID);
	return count != null;
}
/**
 * Insert the method's description here.
 * Creation date: (8/14/2003 10:44:56 AM)
 * @return boolean
 * @param row int
 */
public boolean isReady(int r) {
	DisplayPackageInfo p = (DisplayPackageInfo) rows.elementAt(r);
	return p.getPackageStatus() == DropboxGenerator.STATUS_PARTIAL;
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
