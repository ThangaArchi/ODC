package oem.edge.ed.odc.dsmp.client;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
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
 * Creation date: (10/22/2002 11:11:41 AM)
 * @author: Mike Zarnick
 */
public class TableMap extends AbstractTableModel implements TableModelListener {
	protected TableModel model;
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 11:19:34 AM)
 * @return java.lang.Class
 * @param column int
 */
public Class getColumnClass(int column) {
	return model.getColumnClass(column);
}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return (model == null) ? 0 : model.getColumnCount();
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 11:18:46 AM)
 * @return java.lang.String
 * @param column int
 */
public String getColumnName(int column) {
	return model.getColumnName(column);
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 11:13:31 AM)
 * @return javax.swing.table.TableModel
 */
public TableModel getModel() {
	return model;
}
/**
 * getRowCount method comment.
 */
public int getRowCount() {
	return (model == null) ? 0 : model.getRowCount();
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int column) {
	return model.getValueAt(row, column);
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 11:20:40 AM)
 * @return boolean
 * @param row int
 * @param column int
 */
public boolean isCellEditable(int row, int column) {
	return model.isCellEditable(row, column);
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 11:13:31 AM)
 * @param newModel javax.swing.table.TableModel
 */
public void setModel(TableModel newModel) {
	model = newModel;
	model.addTableModelListener(this);
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 11:16:58 AM)
 * @param value java.lang.Object
 * @param row int
 * @param column int
 */
public void setValueAt(Object value, int row, int column) {
	model.setValueAt(value, row, column);
}
/**
 * This fine grain notification tells listeners the exact range
 * of cells, rows, or columns that changed.
 */
public void tableChanged(TableModelEvent e) {
	fireTableChanged(e);
}
}
