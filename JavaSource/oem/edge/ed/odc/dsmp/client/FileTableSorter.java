package oem.edge.ed.odc.dsmp.client;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
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
 * Creation date: (10/26/2002 10:35:19 AM)
 * @author: Mike Zarnick
 */
public class FileTableSorter extends TableSorter {
/**
 * FileTableSorter constructor comment.
 */
public FileTableSorter() {
	super();
	sortingColumn = 1;
}
/**
 * FileTableSorter constructor comment.
 * @param model javax.swing.table.TableModel
 */
public FileTableSorter(TableModel model) {
	super(model);
	sortingColumn = 1;
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/2002 10:44:40 AM)
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
			// We don't allow sorting by file/dir, that is implicit always.
			if (e.getClickCount() == 1 && column > 0) {
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
 * Creation date: (10/26/2002 10:41:32 AM)
 * @return int
 * @param row1 int
 * @param row2 int
 */
public int compare(int row1, int row2) {
	// We compare by dir/file first.
	int r = compareRowsByColumn(row1,row2,0);

	if (r != 0) // directory vs file always ascending.
		return r;

	r = super.compare(row1,row2);

	if (sortingColumn == 1 || r != 0)
		return r;

	// Not sorting by name and entries are so far equal,
	// use name to order.

	r = compareRowsByColumn(row1,row2,1);

	return (r == 0 || ascending) ? r : -r;
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/2002 10:53:46 AM)
 * @return int
 * @param row1 int
 * @param row2 int
 * @param column int
 */
public int compareRowsByColumn(int row1, int row2, int column) {
	if (column > 0)
		return super.compareRowsByColumn(row1,row2,column);

	// We need to handle the dir/file boolean. The default
	// method orders false lower than true, we need dirs (true)
	// to be lower.
	Boolean b1 = (Boolean) model.getValueAt(row1, column);
	Boolean b2 = (Boolean) model.getValueAt(row2, column); 

	int result = 0;

	if (b1 == null && b2 == null)
		result = 0;
	else if (b1 == null)
		result = -1;
	else if (b2 == null)
		result = 1;
	else if (b1.booleanValue() == b2.booleanValue())
		result = 0;
	else if (b1.booleanValue())
		result = -1;
	else
		result = 1;

	return result;
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/2002 10:38:14 AM)
 * @param e javax.swing.event.TableModelEvent
 */
public void tableChanged(TableModelEvent e) {
	sortingColumn = 1;
	super.tableChanged(e);
}
}
