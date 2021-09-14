package oem.edge.ed.odc.ftp.client;

import java.io.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import javax.swing.event.TableModelEvent;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
/**
 * Insert the type's description here.
 * Creation date: (10/22/2002 11:24:30 AM)
 * @author: Mike Zarnick
 */
public class TableSorter extends TableMap {
	int indices[];
	int sortingColumn = -1;
	boolean ascending = true;
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 11:24:57 AM)
 */
public TableSorter() {
	indices = new int[0];
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 11:25:24 AM)
 * @param model javax.swing.table.TableModel
 */
public TableSorter(TableModel model) {
	setModel(model);
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 1:19:15 PM)
 * @param table javax.swing.JTable
 */
public void addMouseListenerToHeaderInTable(JTable table) {
	final TableSorter sorter = this;
	final JTable tableView = table;
	tableView.setColumnSelectionAllowed(false);
	MouseAdapter listMouseListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			TableColumnModel cm = tableView.getColumnModel();
			int viewColumn = cm.getColumnIndexAtX(e.getX());
			int column = tableView.convertColumnIndexToModel(viewColumn);
			if (e.getClickCount() == 1 && column != -1) {
				if (sorter.sortingColumn == column)
					sorter.reverseSort();
				else
					sorter.sortByColumn(column,true);
				tableView.getTableHeader().repaint();
			}
		}
	};
	JTableHeader th = tableView.getTableHeader();
	th.addMouseListener(listMouseListener);
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 12:32:20 PM)
 */
public void checkModel() {
	if (indices.length != model.getRowCount()) {
		System.err.println("Sorter not informed of a change in model.");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 12:58:50 PM)
 * @return int
 * @param row1 int
 * @param row2 int
 */
public int compare(int row1, int row2) {
	int r = compareRowsByColumn(row1,row2,sortingColumn);

	return (r == 0 || ascending) ? r : -r;
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 11:29:07 AM)
 * @return int
 * @param row1 int
 * @param row2 int
 * @param column int
 */
public int compareRowsByColumn(int row1, int row2, int column) {
	Class type = model.getColumnClass(column);

	Object o1 = model.getValueAt(row1, column);
	Object o2 = model.getValueAt(row2, column); 

	int result = 0;

	if (o1 == null && o2 == null) {
		result = 0;
	}
	else if (o1 == null) {
		result = -1;
	}
	else if (o2 == null) {
		result = 1;
	}
	else if (type == Long.class) {
		Long L1 = (Long) o1;
		long l1 = L1.longValue();
		Long L2 = (Long) o2;
		long l2 = L2.longValue();

		if (l1 < l2)
			result = -1;
		else if (l1 > l2)
			result = 1;
		else
			result = 0;
	}
	else if (type.getSuperclass() == java.lang.Number.class) {
		Number n1 = (Number) o1;
		double d1 = n1.doubleValue();
		Number n2 = (Number) o2;
		double d2 = n2.doubleValue();

		if (d1 < d2)
			result = -1;
		else if (d1 > d2)
			result = 1;
		else
			result = 0;
	} else if (type == java.util.Date.class) {
		Date d1 = (Date) o1;
		long n1 = d1.getTime();
		Date d2 = (Date) o2;
		long n2 = d2.getTime();

		if (n1 < n2)
			result = -1;
		else if (n1 > n2)
			result = 1;
		else
			result = 0;
	} else if (type == String.class) {
		String s1 = (String) o1;
		String s2 = (String) o2;
		result = s1.compareTo(s2);
	} else if (type == File.class) {
		File f1 = (File) o1;
		String s1 = f1.getName();
		File f2 = (File) o2;
		String s2 = f2.getName();
		result = s1.compareTo(s2);
	} else if (type == Boolean.class) {
		Boolean bool1 = (Boolean) o1;
		boolean b1 = bool1.booleanValue();
		Boolean bool2 = (Boolean) o2;
		boolean b2 = bool2.booleanValue();

		if (b1 == b2)
			result = 0;
		else if (b1)
			result = 1;
		else
			result = -1;
	} else {
		String s1 = o1.toString();
		String s2 = o2.toString();
		result = s1.compareTo(s2);
	}

	return result;
}
/**
 * Insert the method's description here.
 * Creation date: (11/6/2002 11:22:42 AM)
 * @return int
 * @param name java.lang.String
 */
public int getSortedIndex(int origIndex) {
	for (int i = 0; i < indices.length; i++) {
		if (indices[i] == origIndex)
			return i;
	}

	return -1;
}
/**
 * Insert the method's description here.
 * Creation date: (11/6/2002 11:22:42 AM)
 * @return int
 * @param name java.lang.String
 */
public int getUnsortedIndex(int index) {
	return indices[index];
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 1:13:28 PM)
 * @return java.lang.Object
 * @param row int
 * @param column int
 */
public Object getValueAt(int row, int column) {
	checkModel();
	return model.getValueAt(indices[row],column);
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 11:48:24 AM)
 */
public void reallocateIndices() {
	int rows = model.getRowCount();

	indices = new int[rows];

	for (int i = 0; i < rows; i++)
		indices[i] = i;

	if (sortingColumn != -1)
		sort();
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 1:30:13 PM)
 */
public void reverseSort() {
	ascending = ! ascending;

	int high = indices.length;
	int mid = high / 2;

	for (int i = 0; i < mid; i++) {
		high--;
		swap(i,high);
	}

	super.tableChanged(new TableModelEvent(this));
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 11:27:50 AM)
 * @param model javax.swing.table.TableModel
 */
public void setModel(TableModel model) {
	super.setModel(model);
	reallocateIndices();
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 1:14:36 PM)
 * @param value java.lang.Object
 * @param row int
 * @param column int
 */
public void setValueAt(Object value, int row, int column) {
	checkModel();
	model.setValueAt(value,indices[row],column);
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 1:01:35 PM)
 * @param from int[]
 * @param to int[]
 * @param low int
 * @param high int
 */
public void shuttleSort(int[] from, int[] to, int low, int high) {
	if (high - low < 2)
		return;

	int mid = (low + high) / 2;

	shuttleSort(to,from,low,mid);
	shuttleSort(to,from,mid,high);

	int p = low;
	int q = mid;

	if (high - low >= 4 && compare(from[mid-1],from[mid]) <= 0) {
		for (int i = low; i < high; i++)
			to[i] = from[i];
		return;
	}

	for (int i = low; i < high; i++) {
		if (q >= high || (p < mid && compare(from[p],from[q]) <= 0))
			to[i] = from[p++];
		else
			to[i] = from[q++];
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 12:55:58 PM)
 */
public void sort() {
	checkModel();
	shuttleSort((int[]) indices.clone(),indices,0,indices.length);
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 1:15:47 PM)
 * @param column int
 * @param ascending boolean
 */
public void sortByColumn(int column, boolean ascending) {
	this.ascending = ascending;
	sortingColumn = column;
	sort();
	super.tableChanged(new TableModelEvent(this));
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 1:32:18 PM)
 * @param i int
 * @param j int
 */
public void swap(int i, int j) {
	int tmp = indices[i];
	indices[i] = indices[j];
	indices[j] = tmp;
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 11:50:42 AM)
 * @param e javax.swing.event.TableModelEvent
 */
public void tableChanged(TableModelEvent e) {
	reallocateIndices();
	super.tableChanged(e);
}
}
