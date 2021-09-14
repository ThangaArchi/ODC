package oem.edge.ed.odc.dropbox.client;

import javax.swing.table.TableCellRenderer;
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

public interface DropboxTableModel extends TableModel {
/**
 * Insert the method's description here.
 * Creation date: (7/22/2004 12:54:18 PM)
 * @return javax.swing.table.TableCellRenderer
 * @param row int
 */
public TableCellRenderer getColumnRenderer(int column);
/**
 * Insert the method's description here.
 * Creation date: (7/22/2004 12:55:40 PM)
 * @return int
 * @param row int
 */
public int getColumnWidth(int column);
/**
 * Insert the method's description here.
 * Creation date: (7/22/2004 12:55:40 PM)
 * @return int
 * @param row int
 */
public String getLogicalColumnName(int column);
}
