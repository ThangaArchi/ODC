package oem.edge.ed.odc.ftp.client;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import java.io.*;
import oem.edge.ed.odc.ftp.common.*;

/**
 * Insert the type's description here.
 * Creation date: (10/21/2002 9:56:37 AM)
 * @author: Mike Zarnick
 */
public class FileTableModel extends AbstractTableModel {
	static public String[] headings = { "", "Name", "Size", "Date" };
	static public Long MinusOne = new Long(-1);
	Boolean[] isDir = null;
	String[] files = null;
	Long[] lSizes = null;
	long[] sizes = null;
	Long[] dates = null;
	String dir = null;
/**
 * FileListModel constructor comment.
 */
public FileTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 2:49:51 PM)
 * @param column int
 */
public Class getColumnClass(int column) {
	switch (column) {
		case 0: return Boolean.class;
		case 1: return String.class;
		default: return Long.class;
	}
}
/**
 * getSize method comment.
 */
public int getColumnCount() {
	return headings.length;
}
/**
 * getSize method comment.
 */
public String getColumnName(int column) {
	return headings[column];
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2002 3:52:10 PM)
 * @return java.io.File
 */
public String getDirectory() {
	return dir;
}
/**
 * getFileIndex searches the table for the specified name. If the name is found, its index is returned. -1
 * is returned otherwise.
 */
public int getFileIndex(String name) {
	if (files != null) {
		for (int i = 0; i < files.length; i++) {
			if (files[i].equals(name))
				return i;
		}
	}

	return -1;
}
/**
 * getFileLength searches the table for the specified name. If the name is found and it is a file,
 * its reported size is returned. Otherwise 0 is returned.
 */
public long getFileLength(String name) {
	int i = getFileIndex(name);

	if (i != -1 && ! isDir[i].booleanValue())
		return sizes[i];
	else
		return 0;
}
/**
 * getFileName returns the file name for the specified row.
 */
public String getFileName(int row) {
	return files[row];
}
/**
 * getSize method comment.
 */
public int getRowCount() {
	if (files != null)
		return files.length;

	return 0;
}
/**
 * getElementAt method comment.
 */
public Object getValueAt(int row,int column) {
	if (files != null && row < files.length) {
		switch (column) {
			case 0: return isDir[row];
			case 1: return files[row];
			case 2: return lSizes[row];
			case 3: return dates[row];
			default: return null;
		}
	}

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (10/21/2002 9:59:29 AM)
 */
public void populateLocal() {
	File f = new File(dir);

	if (f != null && f.exists() && f.isDirectory()) {
		files = f.list();
		isDir = new Boolean[files.length];
		lSizes = new Long[files.length];
		sizes = new long[files.length];
		dates = new Long[files.length];
		for (int i = 0; i < files.length; i++) {
			File entry = new File(f,files[i]);
			isDir[i] = entry.isDirectory() ? Boolean.TRUE : Boolean.FALSE;
			if (isDir[i].booleanValue()) {
				sizes[i] = -1;
				lSizes[i] = MinusOne;
			}
			else {
				sizes[i] = entry.length();
				lSizes[i] = new Long(sizes[i]);
			}
			dates[i] = new Long(entry.lastModified());
		}
	}

	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (10/21/2002 9:59:29 AM)
 */
public void populateRemote(Vector data) {
	if (data != null && data.size() > 0) {
		isDir = new Boolean[data.size()];
		files = new String[data.size()];
		lSizes = new Long[data.size()];
		sizes = new long[data.size()];
		dates = new Long[data.size()];
		for (int i = 0; i < data.size(); i++) {
			AreaContent ac = (AreaContent) data.elementAt(i);
			files[i] = ac.getName();
			isDir[i] = ac.getType() == AreaContent.TYPE_DIRECTORY ? Boolean.TRUE : Boolean.FALSE;
			if (isDir[i].booleanValue()) {
				sizes[i] = -1;
				lSizes[i] = MinusOne;
			}
			else {
				sizes[i] = ac.getSize();
				lSizes[i] = new Long(sizes[i]);
			}
			dates[i] = new Long(ac.getTimeDate());
		}
	}

	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (10/21/2002 10:02:20 AM)
 */
public void refresh() {
	setDirectory(dir);
}
/**
 * Insert the method's description here.
 * Creation date: (10/21/2002 9:59:29 AM)
 * @param dir String
 */
public void setDirectory(String dir) {
	isDir = null;
	files = null;
	lSizes = null;
	sizes = null;
	dates = null;
	this.dir = dir;

	fireTableDataChanged();
}
}
