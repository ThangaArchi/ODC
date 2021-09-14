package oem.edge.ed.odc.meeting.clienta;

import java.awt.*;

/**
 * Insert the type's description here.
 * Creation date: (8/23/2002 1:09:16 PM)
 * @author: Mike Zarnick
 */
class TipButton extends Button implements TipOwner {
	private String tipText = null;
/**
 * TipButton constructor comment.
 */
public TipButton() {
	super();
}
/**
 * TipButton constructor comment.
 * @param label java.lang.String
 */
public TipButton(String label) {
	super(label);
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2002 1:10:11 PM)
 * @return java.lang.String
 */
public String getTipText() {
	return tipText;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2002 1:10:36 PM)
 * @param t java.lang.String
 */
public void setTipText(String t) {
	if (tipText != null)
		TipManager.getTipManager().remove((TipOwner) this);

	tipText = t;

	if (tipText != null)
		TipManager.getTipManager().add(this);
}
}
