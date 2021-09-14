package oem.edge.ed.odc.dropbox.client;

/**
 * Insert the type's description here.
 * Creation date: (1/15/2003 2:56:51 PM)
 * @author: Mike Zarnick
 */

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import oem.edge.ed.odc.dropbox.common.FileInfo;
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

public class OutboxFileTM extends AbstractTableModel implements DropboxTableModel {
	private int[] columnWidths = { 225, 85, 225 };
	private String[] columnNames = { "Name", "Size", "MD5 Value" };
	private String[] logicalColumnNames = { "name", "size", "md5" };
	private Class[] columnClasses = { String.class, Long.class, String.class };
	private TableCellRenderer[] columnRenderers = { null, new SizeCellRenderer(), null };
	private Vector rows = new Vector();
	private Vector sizes = new Vector();
/**
 * InboxPkgTM constructor comment.
 */
public OutboxFileTM() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2003 10:00:45 AM)
 * @param v java.util.Vector
 */
public void addFiles(Vector v) {
	sizes.removeAllElements();

	if (v != null) {
		rows = v;
		Enumeration e = v.elements();
		while (e.hasMoreElements()) {
			FileInfo f = (FileInfo) e.nextElement();
			sizes.addElement(new Long(f.getFileSize()));
		}
	}
	else {
		rows.removeAllElements();
	}

	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2003 2:47:45 PM)
 */
public void clear() {
	rows.removeAllElements();
	sizes.removeAllElements();
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
 * Creation date: (2/5/2003 9:52:26 AM)
 * @return long
 * @param r int
 */
public long getFileID(int r) {
	FileInfo f = (FileInfo) rows.elementAt(r);
	return f.getFileId();
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2003 9:52:26 AM)
 * @return FileInfo
 * @param r int
 */
public FileInfo getFileInfo(int r) {
	return (FileInfo) rows.elementAt(r);
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
 * getRowCount method comment.
 */
public int getRowCount() {
	return rows.size();
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int r, int c) {
	FileInfo f = (FileInfo) rows.elementAt(r);

	if (f != null) {
		switch (c) {
			case 0: {
				return f.getFileName();
			}
			case 1: {
				return (Long) sizes.elementAt(r);
			}
			case 2: {
				return f.getFileMD5();
			}
		}
	}

	return null;
}
}
