package oem.edge.ed.odc.dsmp.client;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
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
 * Creation date: (11/6/2002 2:53:27 PM)
 * @author: Mike Zarnick
 */
public class FileStatusDirectionRenderer extends JLabel implements TableCellRenderer {
	static public ImageIcon UP = new ImageIcon(FileStatusDirectionRenderer.class.getResource("/oem/edge/ed/odc/dsmp/client/upload.gif"));
	static public ImageIcon DOWN = new ImageIcon(FileStatusDirectionRenderer.class.getResource("/oem/edge/ed/odc/dsmp/client/download.gif"));
/**
 * FileStatusDirectionRenderer constructor comment.
 */
public FileStatusDirectionRenderer() {
	super();

	setOpaque(true);
	setHorizontalAlignment(JLabel.CENTER);
	setVerticalAlignment(JLabel.CENTER);
}
/**
 * FileStatusDirectionRenderer constructor comment.
 * @param text java.lang.String
 */
public FileStatusDirectionRenderer(String text) {
	super(text);
}
/**
 * FileStatusDirectionRenderer constructor comment.
 * @param text java.lang.String
 * @param horizontalAlignment int
 */
public FileStatusDirectionRenderer(String text, int horizontalAlignment) {
	super(text, horizontalAlignment);
}
/**
 * FileStatusDirectionRenderer constructor comment.
 * @param text java.lang.String
 * @param icon javax.swing.Icon
 * @param horizontalAlignment int
 */
public FileStatusDirectionRenderer(String text, Icon icon, int horizontalAlignment) {
	super(text, icon, horizontalAlignment);
}
/**
 * FileStatusDirectionRenderer constructor comment.
 * @param image javax.swing.Icon
 */
public FileStatusDirectionRenderer(Icon image) {
	super(image);
}
/**
 * FileStatusDirectionRenderer constructor comment.
 * @param image javax.swing.Icon
 * @param horizontalAlignment int
 */
public FileStatusDirectionRenderer(Icon image, int horizontalAlignment) {
	super(image, horizontalAlignment);
}
/**
 *  This method is sent to the renderer by the drawing table to
 *  configure the renderer appropriately before drawing.  Return
 *  the Component used for drawing.
 *
 * @param	table		the JTable that is asking the renderer to draw.
 *				This parameter can be null.
 * @param	value		the value of the cell to be rendered.  It is
 *				up to the specific renderer to interpret
 *				and draw the value.  eg. if value is the
 *				String "true", it could be rendered as a
 *				string or it could be rendered as a check
 *				box that is checked.  null is a valid value.
 * @param	isSelected	true is the cell is to be renderer with
 *				selection highlighting
 * @param	row	        the row index of the cell being drawn.  When
 *				drawing the header the rowIndex is -1.
 * @param	column	        the column index of the cell being drawn
 */
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	if (value == Boolean.FALSE) {
		setIcon(DOWN);
		setToolTipText("File download from server");
	}
	else {
		setIcon(UP);
		setToolTipText("File upload to server");
	}

	if (isSelected) {
		setBackground(table.getSelectionBackground());
		setForeground(table.getSelectionForeground());
	}
	else {
		setBackground(table.getBackground());
		setForeground(table.getForeground());
	}

	return this;
}
}
