package oem.edge.ed.odc.dropbox.client.soa;

/**
 * Insert the type's description here.
 * Creation date: (1/15/2003 2:56:51 PM)
 * @author: Mike Zarnick
 */

import oem.edge.ed.odc.dropbox.client.DropboxTableModel;
import oem.edge.ed.odc.dropbox.client.SizeCellRenderer;
import oem.edge.ed.odc.dropbox.common.FileInfo;
import oem.edge.ed.odc.dropbox.common.DropboxGenerator;
import oem.edge.ed.odc.dsmp.client.FileStatusEvent;
import oem.edge.ed.odc.dsmp.client.FileStatusListener;

import java.util.*;
import java.io.*;
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

public class SendFileTM extends AbstractTableModel implements DropboxTableModel,FileStatusListener {
	private int[] columnWidths = { 150, 85, 85, 225, 165 };
	private String[] columnNames = { "Name", "Size", "Status", "MD5 Value", "Source" };
	private String[] logicalColumnNames = { "name", "size", "status", "md5", "source" };
	private Class[] columnClasses = { String.class, Long.class, String.class, String.class, String.class };
	private TableCellRenderer[] columnRenderers = { null, new SizeCellRenderer(), null, null, null };
	private long pkgID = -1;
	private Vector rows = new Vector();
	private Vector sizes = null;
	private Vector idLong = null;
	private Hashtable src = new Hashtable();
	private SourceMgr sourceMgr = null;
/**
 * InboxPkgTM constructor comment.
 */
public SendFileTM() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (2/3/2003 10:00:45 AM)
 * @param v java.util.Vector
 */
public synchronized void addFiles(long pID, Vector v) {
	pkgID = pID;
	rows = v;
	src.clear();
	sizes = new Vector(v.size());
	idLong = new Vector(v.size());

	Enumeration e = v.elements();
	while (e.hasMoreElements()) {
		FileInfo f = (FileInfo) e.nextElement();

		sizes.addElement(new Long(f.getFileSize()));
		Long fID = new Long(f.getFileId());
		idLong.addElement(fID);

		if (sourceMgr != null) {
			String source = sourceMgr.getSourceDir(f.getFileId());
			if (source != null)
				src.put(fID,source);
		}
	}

	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2003 2:47:45 PM)
 */
public synchronized void clear() {
	rows.removeAllElements();
	sizes = null;
	idLong = null;
	src.clear();
	pkgID = -1;
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

	// Not interested if not our package.
	if (e.packageID != pkgID)
		return;

	// If event is for a file upload which has begun.
	if (e.isFileBegun()) {
		// Attempt to find the file, if found, it is a restart.
		for (int i = 0; i < rows.size(); i++) {
			FileInfo f = (FileInfo) rows.elementAt(i);
			if (f.getFileId() == e.info.getFileId()) {
				f.setFileStatus(e.info.getFileStatus());
				f.setFileSize(e.info.getFileSize());
				sizes.setElementAt(new Long(f.getFileSize()),i);
				fireTableRowsUpdated(i,i);
				return;
			}
		}

		// Did not find file, add it as new.
		int i = rows.size();
		rows.addElement(e.info);
		sizes.addElement(new Long(e.info.getFileSize()));
		Long fID = new Long(e.info.getFileId());
		idLong.addElement(fID);
		// Like addFiles, we could ask sourceMgr for directory, but
		// he should get this same event, so we take it from here.
		src.put(fID,e.sourceDir);
		fireTableRowsInserted(i,i);
	}

	// If event is for an ended file upload.
	else if (e.isFileEnded()) {
		// Try to find the file.
		for (int i = 0; i < rows.size(); i++) {
			FileInfo f = (FileInfo) rows.elementAt(i);
			if (f.getFileId() == e.info.getFileId()) {
				f.setFileStatus(e.info.getFileStatus());
				f.setFileMD5(e.info.getFileMD5());
				f.setFileSize(e.info.getFileSize());
				sizes.setElementAt(new Long(f.getFileSize()),i);
				if (e.info.getFileStatus() == DropboxGenerator.STATUS_COMPLETE) {
					src.remove(new Long(f.getFileId()));
				}
				fireTableRowsUpdated(i,i);
				return;
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
 * @return long
 * @param r int
 */
public long getFileLength(int r) {
	FileInfo f = (FileInfo) rows.elementAt(r);
	return f.getFileSize();
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2003 1:04:02 PM)
 * @return long
 * @param name java.lang.String
 */
public long getFileLength(String name) {
	int i = getFileRow(name);

	if (i != -1)
		return getFileLength(i);

	return 0;
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2003 9:52:26 AM)
 * @return long
 * @param r int
 */
public String getFileName(int r) {
	FileInfo f = (FileInfo) rows.elementAt(r);
	return f.getFileName();
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2003 1:04:02 PM)
 * @return long
 * @param name java.lang.String
 */
public int getFileRow(String name) {
	for (int i = 0; i < rows.size(); i++) {
		FileInfo f = (FileInfo) rows.elementAt(i);
		if (f.getFileName().equals(name)) {
			return i;
		}
	}

	return -1;
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
				return sizes.elementAt(r);
			}
			case 2: {
				if (f.getFileStatus() == DropboxGenerator.STATUS_NONE)
					return "Adding";
				else if (f.getFileStatus() == DropboxGenerator.STATUS_INCOMPLETE)
					return "Stopped";
				else
					return "Added";
			}
			case 3: {
				return f.getFileMD5();
			}
			case 4: {
				Long fID = (Long) idLong.elementAt(r);
				return src.get(fID);
			}
		}
	}

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (2/12/2003 1:31:32 PM)
 * @return boolean
 * @param r int
 */
public boolean isFailed(int r) {
	FileInfo f = (FileInfo) rows.elementAt(r);
	Long fID = (Long) idLong.elementAt(r);
	return f.getFileStatus() == DropboxGenerator.STATUS_INCOMPLETE && src.get(fID) != null;
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2003 9:09:01 AM)
 * @param size long
 * @param row int
 */
public void setFileSize(long size, int r) {
	FileInfo f = (FileInfo) rows.elementAt(r);
	f.setFileSize(size);
	sizes.setElementAt(new Long(size),r);
	fireTableRowsUpdated(r,r);
}
/**
 * Insert the method's description here.
 * Creation date: (2/27/2003 10:23:28 AM)
 * @param mgr oem.edge.ed.odc.dropbox.client.SourceMgr
 */
public void setSourceMgr(SourceMgr mgr) {
	sourceMgr = mgr;
}
}
