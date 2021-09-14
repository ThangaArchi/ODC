package oem.edge.ed.odc.meeting.client;

import javax.swing.tree.*;
import javax.swing.*;
import java.awt.*;
/**
 * Insert the type's description here.
 * Creation date: (3/19/2003 11:54:39 AM)
 * @author: Mike Zarnick
 */
public class PresenceCellRenderer extends DefaultTreeCellRenderer {
	static public Color present = new Color(0,130,0);
	static public Color absent = Color.black;
	static public Color invalid = new Color(170,0,0);
	// Icon ownerImage = null;
	// Icon controlImage = null;
	// Icon blankImage = null;
	Icon blankBlankImage = null;
	Icon blankBatonImage = null;
	Icon blankModImage = null;
	Icon ownerBlankImage = null;
	Icon ownerModImage = null;
	Icon ownerBatonImage = null;
	Icon blankGroupImage = null;
	Icon blankProjectImage = null;
	private boolean limitChatter = false;
	private DSMPDispatcher dispatcher = null;
/**
 * PresenceCellRenderer constructor comment.
 */
public PresenceCellRenderer() {
	super();
	blankBlankImage = new ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/blank_blank.gif"));
	blankBatonImage = new ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/blank_baton.gif"));
	blankModImage = new ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/blank_mod.gif"));
	ownerBlankImage = new ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/owner_blank.gif"));
	ownerBatonImage = new ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/owner_baton.gif"));
	ownerModImage = new ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/owner_mod.gif"));
	blankGroupImage = new ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/blank_group.gif"));
	blankProjectImage = new ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/blank_project.gif"));
}
	/**
	 * Sets the value of the current tree cell to <code>value</code>.
	 * If <code>selected</code> is true, the cell will be drawn as if
	 * selected. If <code>expanded</code> is true the node is currently
	 * expanded and if <code>leaf</code> is true the node represets a
	 * leaf anf if <code>hasFocus</code> is true the node currently has
	 * focus. <code>tree</code> is the JTree the receiver is being
	 * configured for.
	 * Returns the Component that the renderer uses to draw the value.
	 *
	 * @return	Component that the renderer uses to draw the value.
	 */
public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
	//System.out.println("Render: sel is " + selected + ", exp is " + expanded + ", leaf is " + leaf + ", row is " + row);
	Component comp = super.getTreeCellRendererComponent(tree,value,selected,expanded,leaf,row,hasFocus);

	if (comp instanceof JLabel) {
		JLabel r = (JLabel) comp;
		if (value instanceof Invite) {
			Invite i = (Invite) value;
			r.setText(i.name);

			if (value instanceof UserInvite) {
				UserInvite ui = (UserInvite) i;
				Color c = null;
				if (ui.present)
					if (! limitChatter || (dispatcher != null && dispatcher.isOwner) || ui.isOwner)
						c = present;
					else
						c = invalid;
				else if (ui.inviteID == -1)
					c = invalid;
				else
					c = absent;

				if (selected) {
					r.setBackground(c);
					r.setForeground(tree.getBackground());
				}
				else {
					r.setForeground(c);
					r.setBackground(tree.getBackground());
				}

				if (ui.isOwner)
					if (ui.isModerator)
						r.setIcon(ownerModImage);
					else if (ui.inControl)
						r.setIcon(ownerBatonImage);
					else
						r.setIcon(ownerBlankImage);
				else if (ui.isModerator)
					r.setIcon(blankModImage);
				else if (ui.inControl)
					r.setIcon(blankBatonImage);
				else
					r.setIcon(blankBlankImage);
			}
			else {
				Color c = null;
				if (i.inviteID == -1)
					c = invalid;
				else
					c = absent;

				if (selected) {
					r.setBackground(c);
					r.setForeground(tree.getBackground());
				}
				else {
					r.setForeground(c);
					r.setBackground(tree.getBackground());
				}

				if (value instanceof GroupInvite)
					r.setIcon(blankGroupImage);
				else
					r.setIcon(blankProjectImage);
			}
		}
		else {
			r.setText("Unknown value object");
			r.setForeground(tree.getForeground());
			r.setBackground(tree.getBackground());
			r.setIcon(null);
		}
	}

	return comp;
}
/**
 * Insert the method's description here.
 * Creation date: (7/1/2004 2:21:12 PM)
 * @return boolean
 */
public boolean isLimitChatter() {
	return limitChatter;
}
/**
 * Insert the method's description here.
 * Creation date: (7/2/2004 2:34:47 PM)
 * @param dispatcher oem.edge.ed.odc.meeting.client.DSMPDispatcher
 */
public void setDispatcher(DSMPDispatcher dispatcher) {
	this.dispatcher = dispatcher;
}
/**
 * Insert the method's description here.
 * Creation date: (7/1/2004 2:21:12 PM)
 * @param newLimitProjectChatter boolean
 */
public void setLimitChatter(boolean newLimitChatter) {
	limitChatter = newLimitChatter;
}
}
